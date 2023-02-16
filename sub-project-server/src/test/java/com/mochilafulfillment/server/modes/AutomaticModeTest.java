package com.mochilafulfillment.server.modes;

import com.mochilafulfillment.server.Agv;
import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.agv_utils.Exceptions.PGVException;
import com.mochilafulfillment.server.api.PickPath;
import com.mochilafulfillment.server.api.laptop.TerminalReader;
import com.mochilafulfillment.server.controls_logic.ForkLift;
import com.mochilafulfillment.server.controls_logic.Rotation;
import com.mochilafulfillment.server.controls_logic.StopOnTag;
import com.mochilafulfillment.server.controls_logic.StraightLine;
import com.mochilafulfillment.server.controls_logic.dtos.PathRecord;
import com.mochilafulfillment.server.controls_logic.utils.Lqr;
import com.mochilafulfillment.server.controls_logic.utils.Pid;
import com.mochilafulfillment.server.dtos.AgvInputsRecord;
import com.mochilafulfillment.server.dtos.AgvOutputsRecord;
import com.mochilafulfillment.server.motor_controller.dtos.MotorControllerRecord;
import com.mochilafulfillment.server.position_scanner.dtos.PositionScannerResponseRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AutomaticModeTest {

    AutomaticMode testClass;

    @BeforeEach
    public void init() throws IOException {
        testClass = new AutomaticMode("testpath.csv", Constants.PID_CONTROL);
    }

    @Test
    public void endModeTest(){
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setModeFinished(false);

        testRecord = testClass.endMode(testRecord);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setModeFinished(true);

        Assertions.assertEquals(testRecord, compareRecord);
    }

    @Test
    public void decisionTreeTag() throws InterruptedException {
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setTagIsFinished(false);
        testRecord.setRotationIsFinished(false);
        testRecord.setForkLiftIsFinished(false);
        testRecord = testClass.decisionTree(testRecord);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setSafetyScannerMode(Constants.REGULAR_SCAN);
        compareRecord.setTagIsFinished(false);
        compareRecord.setRotationIsFinished(false);
        compareRecord.setForkLiftIsFinished(false);
        compareRecord.setLock(Constants.STOP_ON_TAG_KEY);

        Assertions.assertEquals(testRecord, compareRecord);
    }

    @Test
    public void decisionTreeRotation() throws InterruptedException {
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setTagIsFinished(true);
        testRecord.setRotationIsFinished(false);
        testRecord.setForkLiftIsFinished(false);
        testRecord.setAgvStopped(true);
        testRecord = testClass.decisionTree(testRecord);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setTagIsFinished(true);
        compareRecord.setRotationIsFinished(false);
        compareRecord.setForkLiftIsFinished(false);
        compareRecord.setAgvStopped(false);
        compareRecord.setSafetyScannerMode(Constants.REGULAR_SCAN);
        compareRecord.setLock(Constants.ROTATION_KEY);
        compareRecord.setTagIsFinished(true);

        Assertions.assertEquals(testRecord, compareRecord);
    }

    @Test
    public void decisionTreeForkLiftTest() throws InterruptedException {
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setTagIsFinished(true);
        testRecord.setRotationIsFinished(true);
        testRecord.setForkLiftIsFinished(false);
        testRecord.setAgvStopped(true);


        testRecord = testClass.decisionTree(testRecord);
        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setSafetyScannerMode(Constants.PICKING_SCAN);
        compareRecord.setLock(Constants.FORK_LIFT_KEY);
        compareRecord.setTagIsFinished(true);
        compareRecord.setRotationIsFinished(true);
        compareRecord.setForkLiftIsFinished(false);
        compareRecord.setAgvStopped(false);



        Assertions.assertEquals(testRecord, compareRecord);
    }

    @Test
    public void decisionTreeNewPathRecordTest() throws InterruptedException {
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setTagIsFinished(true);
        testRecord.setRotationIsFinished(true);
        testRecord.setForkLiftIsFinished(true);
        testRecord.setLock(Constants.FORK_LIFT_KEY);
        testRecord = testClass.decisionTree(testRecord);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setSafetyScannerMode(Constants.REGULAR_SCAN);
        compareRecord.setTagIsFinished(false);
        compareRecord.setRotationIsFinished(false);
        compareRecord.setForkLiftIsFinished(false);
        compareRecord.setLock(0);
        compareRecord.setDirection(Constants.FORWARDS_STRING);
        compareRecord.setNominalVelocity(Constants.STANDARD_VELOCITY);
        compareRecord.setNominalAccel(Constants.STANDARD_ACCELERATION);

        Assertions.assertEquals(testRecord, compareRecord);
    }

    @Test
    public void decisionTreePathRecordEmptyTest() throws InterruptedException {
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setPathFinished(false);

        PickPath mockedPickPath = mock(PickPath.class);
        when(mockedPickPath.getNext()).thenReturn(null);
        testClass.setPickPath(mockedPickPath);

        testRecord = testClass.decisionTree(testRecord);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setPathFinished(true);

        Assertions.assertEquals(testRecord, compareRecord);
    }

    @Test
    public void decisionTreeAGVBackwardsTest() throws InterruptedException {
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setTagIsFinished(true);
        testRecord.setRotationIsFinished(true);
        testRecord.setForkLiftIsFinished(true);

        PathRecord mockedPathRecord = mock(PathRecord.class);
        when(mockedPathRecord.isLowerLine()).thenReturn(true);
        testClass.setPathRecord(mockedPathRecord);

        PickPath mockedPickPath = mock(PickPath.class);
        testClass.setPickPath(mockedPickPath);
        when(mockedPickPath.getNext()).thenReturn(mockedPathRecord);

        testRecord = testClass.decisionTree(testRecord);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setTagIsFinished(false);
        compareRecord.setRotationIsFinished(false);
        compareRecord.setForkLiftIsFinished(false);
        compareRecord.setDirection(Constants.BACKWARDS_STRING);
        compareRecord.setNominalAccel(Constants.BACKWARDS_ACCELERATION);
        compareRecord.setNominalVelocity(Constants.BACKWARDS_VELOCITY);

        Assertions.assertEquals(testRecord, compareRecord);
    }

    @Test
    public void exitAutoModeRunTest() throws InterruptedException, PGVException {
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setPathFinished(false);
        TerminalReader terminalReader = new TerminalReader();
        terminalReader.setExitAutoMode(true);
        testRecord = testClass.run(testRecord, new PositionScannerResponseRecord(), new MotorControllerRecord(), terminalReader);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setPathFinished(true);

        Assertions.assertEquals(testRecord, compareRecord);
    }


    @Test
    public void setInputRecordFromPositionScannerRunTest() throws InterruptedException, PGVException {
        PositionScannerResponseRecord positionScannerRecord = new PositionScannerResponseRecord();
        positionScannerRecord.setNewPositionScannerRecord(true);
        positionScannerRecord.setXPosition(5);
        positionScannerRecord.setYPosition(-5);
        positionScannerRecord.setTapeAngle(2);
        positionScannerRecord.setTagId(6823532);
        positionScannerRecord.setColumns(16);
        positionScannerRecord.setRows(50);
        positionScannerRecord.setOnTape(true);

        testClass.run(new AgvOutputsRecord(), positionScannerRecord, new MotorControllerRecord(), new TerminalReader());

        AgvInputsRecord compareInputRecord = new AgvInputsRecord();
        compareInputRecord.setX(5);
        compareInputRecord.setY(-5);
        compareInputRecord.setTapeAngle(2);
        compareInputRecord.setTagId(6823532);
        compareInputRecord.setColumnNumber(16);
        compareInputRecord.setRowNumber(50);
        compareInputRecord.setOnTape(true);

        PositionScannerResponseRecord comparePositionScannerRecord = new PositionScannerResponseRecord();
        comparePositionScannerRecord.setNewPositionScannerRecord(false);
        comparePositionScannerRecord.setXPosition(5);
        comparePositionScannerRecord.setYPosition(-5);
        comparePositionScannerRecord.setTapeAngle(2);
        comparePositionScannerRecord.setTagId(6823532);
        comparePositionScannerRecord.setColumns(16);
        comparePositionScannerRecord.setRows(50);
        comparePositionScannerRecord.setOnTape(true);

        Assertions.assertEquals(comparePositionScannerRecord, positionScannerRecord);
        Assertions.assertEquals(compareInputRecord, testClass.getAgvInputsRecord());
    }

    @Test
    public void doNotSetInputRecordFromPositionScannerRunTest() throws InterruptedException, PGVException {
        PositionScannerResponseRecord positionScannerRecord = new PositionScannerResponseRecord();
        positionScannerRecord.setNewPositionScannerRecord(false);
        positionScannerRecord.setXPosition(5);
        positionScannerRecord.setYPosition(-5);
        positionScannerRecord.setTapeAngle(2);
        positionScannerRecord.setTagId(6823532);
        positionScannerRecord.setColumns(16);
        positionScannerRecord.setRows(50);
        positionScannerRecord.setOnTape(true);

        testClass.run(new AgvOutputsRecord(), positionScannerRecord, new MotorControllerRecord(), new TerminalReader());

        PositionScannerResponseRecord comparePositionScannerRecord = new PositionScannerResponseRecord();
        comparePositionScannerRecord.setNewPositionScannerRecord(false);
        comparePositionScannerRecord.setXPosition(5);
        comparePositionScannerRecord.setYPosition(-5);
        comparePositionScannerRecord.setTapeAngle(2);
        comparePositionScannerRecord.setTagId(6823532);
        comparePositionScannerRecord.setColumns(16);
        comparePositionScannerRecord.setRows(50);
        comparePositionScannerRecord.setOnTape(true);

        AgvInputsRecord compareInputRecord = new AgvInputsRecord();

        Assertions.assertEquals(positionScannerRecord, comparePositionScannerRecord);
        Assertions.assertEquals(compareInputRecord, testClass.getAgvInputsRecord());
    }

    @Test
    public void setInputRecordFromControllerRunTest() throws InterruptedException, PGVException {
        MotorControllerRecord controllerRecord = new MotorControllerRecord();
        controllerRecord.setNewMotorControllerRecord(true);
        controllerRecord.setTopLiftSensor(true);
        controllerRecord.setMiddleLiftSensor(true);
        controllerRecord.setBottomLiftSensor(true);

        testClass.run(new AgvOutputsRecord(), new PositionScannerResponseRecord(), controllerRecord, new TerminalReader());

        AgvInputsRecord compareInputRecord = new AgvInputsRecord();
        compareInputRecord.setTopSensorTriggered(true);
        compareInputRecord.setMiddleSensorTriggered(true);
        compareInputRecord.setBottomSensorTriggered(true);

        MotorControllerRecord compareControllerRecord = new MotorControllerRecord();
        compareControllerRecord.setNewAgvToMotorControllerRecord(false);
        compareControllerRecord.setTopLiftSensor(true);
        compareControllerRecord.setMiddleLiftSensor(true);
        compareControllerRecord.setBottomLiftSensor(true);

        Assertions.assertEquals(controllerRecord, compareControllerRecord);
        Assertions.assertEquals(testClass.getAgvInputsRecord(), compareInputRecord);
    }

    @Test
    public void doNotSetInputRecordFromControllerRunTest() throws InterruptedException, PGVException {
        MotorControllerRecord controllerRecord = new MotorControllerRecord();
        controllerRecord.setNewMotorControllerRecord(false);
        controllerRecord.setTopLiftSensor(true);
        controllerRecord.setMiddleLiftSensor(true);
        controllerRecord.setBottomLiftSensor(true);

        testClass.run(new AgvOutputsRecord(), new PositionScannerResponseRecord(), controllerRecord, new TerminalReader());

        AgvInputsRecord compareInputRecord = new AgvInputsRecord();

        MotorControllerRecord compareControllerRecord = new MotorControllerRecord();
        compareControllerRecord.setNewAgvToMotorControllerRecord(false);
        compareControllerRecord.setTopLiftSensor(true);
        compareControllerRecord.setMiddleLiftSensor(true);
        compareControllerRecord.setBottomLiftSensor(true);

        Assertions.assertEquals(controllerRecord, compareControllerRecord);
        Assertions.assertEquals(testClass.getAgvInputsRecord(), compareInputRecord);
    }

    @Test
    public void straightLineRunTest() throws InterruptedException, PGVException {
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setTagIsFinished(false);
        testRecord.setRotationIsFinished(false);
        testRecord.setForkLiftIsFinished(false);
        StraightLine mockedStraightLine = mock(StraightLine.class);
        StopOnTag mockedStopOnTag = mock(StopOnTag.class);
        Rotation mockedRotation = mock(Rotation.class);
        ForkLift mockedForkLift = mock(ForkLift.class);

        when(mockedStraightLine.run(testRecord, testClass.getAgvInputsRecord())).thenReturn(testRecord);
        when(mockedForkLift.run(testRecord, testClass.getAgvInputsRecord(), testClass.getPathRecord())).thenReturn(testRecord);
        when(mockedStopOnTag.run(testRecord, testClass.getAgvInputsRecord(), testClass.getPathRecord())).thenReturn(testRecord);
        when(mockedRotation.run(testRecord, testClass.getAgvInputsRecord(), testClass.getPathRecord())).thenReturn(testRecord);

        testClass.setStraightLine(mockedStraightLine);
        testClass.setStopOnTag(mockedStopOnTag);


        testRecord = testClass.run(testRecord, new PositionScannerResponseRecord(), new MotorControllerRecord(), new TerminalReader());
        Mockito.verify(mockedStraightLine).run(testRecord, testClass.getAgvInputsRecord());
        Mockito.verify(mockedStopOnTag).run(testRecord, testClass.getAgvInputsRecord(), testClass.getPathRecord());
        Mockito.verify(mockedForkLift, Mockito.never()).run(testRecord, testClass.getAgvInputsRecord(), testClass.getPathRecord());
        Mockito.verify(mockedRotation, Mockito.never()).run(testRecord, testClass.getAgvInputsRecord(), testClass.getPathRecord());
    }

    @Test
    public void stopOnTagRunTest() throws InterruptedException, PGVException {
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setLock(Constants.STOP_ON_TAG_KEY);
        testRecord.setTagIsFinished(false);
        testRecord.setRotationIsFinished(false);
        testRecord.setForkLiftIsFinished(false);
        StraightLine mockedStraightLine = mock(StraightLine.class);
        StopOnTag mockedStopOnTag = mock(StopOnTag.class);
        Rotation mockedRotation = mock(Rotation.class);
        ForkLift mockedForkLift = mock(ForkLift.class);

        when(mockedStraightLine.run(testRecord, testClass.getAgvInputsRecord())).thenReturn(testRecord);
        when(mockedForkLift.run(testRecord, testClass.getAgvInputsRecord(), testClass.getPathRecord())).thenReturn(testRecord);
        when(mockedStopOnTag.run(testRecord, testClass.getAgvInputsRecord(), testClass.getPathRecord())).thenReturn(testRecord);
        when(mockedRotation.run(testRecord, testClass.getAgvInputsRecord(), testClass.getPathRecord())).thenReturn(testRecord);

        testClass.setStraightLine(mockedStraightLine);
        testClass.setStopOnTag(mockedStopOnTag);


        testRecord = testClass.run(testRecord, new PositionScannerResponseRecord(), new MotorControllerRecord(), new TerminalReader());
        Mockito.verify(mockedStraightLine).run(testRecord, testClass.getAgvInputsRecord());
        Mockito.verify(mockedForkLift, Mockito.never()).run(testRecord, testClass.getAgvInputsRecord(), testClass.getPathRecord());
        Mockito.verify(mockedStopOnTag).run(testRecord, testClass.getAgvInputsRecord(), testClass.getPathRecord());
        Mockito.verify(mockedRotation, Mockito.never()).run(testRecord, testClass.getAgvInputsRecord(), testClass.getPathRecord());
    }

    @Test
    public void rotationRunTest() throws InterruptedException, PGVException {
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setLock(Constants.ROTATION_KEY);
        testRecord.setTagIsFinished(true);
        testRecord.setRotationIsFinished(false);
        testRecord.setForkLiftIsFinished(false);
        StraightLine mockedStraightLine = mock(StraightLine.class);
        StopOnTag mockedStopOnTag = mock(StopOnTag.class);
        Rotation mockedRotation = mock(Rotation.class);
        ForkLift mockedForkLift = mock(ForkLift.class);

        when(mockedStraightLine.run(testRecord, testClass.getAgvInputsRecord())).thenReturn(testRecord);
        when(mockedForkLift.run(testRecord, testClass.getAgvInputsRecord(), testClass.getPathRecord())).thenReturn(testRecord);
        when(mockedStopOnTag.run(testRecord, testClass.getAgvInputsRecord(), testClass.getPathRecord())).thenReturn(testRecord);
        when(mockedRotation.run(testRecord, testClass.getAgvInputsRecord(), testClass.getPathRecord())).thenReturn(testRecord);


        testClass.setRotation(mockedRotation);

        testRecord = testClass.run(testRecord, new PositionScannerResponseRecord(), new MotorControllerRecord(), new TerminalReader());
        Mockito.verify(mockedStraightLine, Mockito.never()).run(testRecord, testClass.getAgvInputsRecord());
        Mockito.verify(mockedForkLift, Mockito.never()).run(testRecord, testClass.getAgvInputsRecord(), testClass.getPathRecord());
        Mockito.verify(mockedStopOnTag, Mockito.never()).run(testRecord, testClass.getAgvInputsRecord(), testClass.getPathRecord());
        Mockito.verify(mockedRotation).run(testRecord, testClass.getAgvInputsRecord(), testClass.getPathRecord());
    }

    @Test
    public void pickRunTest() throws InterruptedException, PGVException {
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setLock(Constants.FORK_LIFT_KEY);
        testRecord.setTagIsFinished(true);
        testRecord.setRotationIsFinished(true);
        testRecord.setForkLiftIsFinished(false);

        StraightLine mockedStraightLine = mock(StraightLine.class);
        StopOnTag mockedStopOnTag = mock(StopOnTag.class);
        Rotation mockedRotation = mock(Rotation.class);
        ForkLift mockedForkLift = mock(ForkLift.class);

        when(mockedStraightLine.run(testRecord, testClass.getAgvInputsRecord())).thenReturn(testRecord);
        when(mockedForkLift.run(testRecord, testClass.getAgvInputsRecord(), testClass.getPathRecord())).thenReturn(testRecord);
        when(mockedStopOnTag.run(testRecord, testClass.getAgvInputsRecord(), testClass.getPathRecord())).thenReturn(testRecord);
        when(mockedRotation.run(testRecord, testClass.getAgvInputsRecord(), testClass.getPathRecord())).thenReturn(testRecord);

        testClass.setForkLift(mockedForkLift);

        testRecord = testClass.run(testRecord, new PositionScannerResponseRecord(), new MotorControllerRecord(), new TerminalReader());
        Mockito.verify(mockedStraightLine, Mockito.never()).run(testRecord, testClass.getAgvInputsRecord());
        Mockito.verify(mockedForkLift).run(testRecord, testClass.getAgvInputsRecord(), testClass.getPathRecord());
        Mockito.verify(mockedStopOnTag, Mockito.never()).run(testRecord, testClass.getAgvInputsRecord(), testClass.getPathRecord());
        Mockito.verify(mockedRotation, Mockito.never()).run(testRecord, testClass.getAgvInputsRecord(), testClass.getPathRecord());
    }

    @Test
    public void emptyPickPathRunTest() throws InterruptedException, PGVException {
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setTagIsFinished(true);
        testRecord.setRotationIsFinished(true);
        testRecord.setForkLiftIsFinished(true);

        PickPath mockedPickPath = mock(PickPath.class);
        when(mockedPickPath.getNext()).thenReturn(null);
        testClass.setPickPath(mockedPickPath);

        testRecord = testClass.run(testRecord, new PositionScannerResponseRecord(), new MotorControllerRecord(), new TerminalReader());

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setDirection(Constants.STOP_STRING);
        compareRecord.setMotor1Velocity(0);
        compareRecord.setMotor2Velocity(0);
        compareRecord.setNominalAccel(Constants.STOPPING_ACCELERATION);
        compareRecord.setMotor1Sign(Constants.MOTOR_POSITIVE);
        compareRecord.setMotor2Sign(Constants.MOTOR_POSITIVE);
        compareRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);
        compareRecord.setPathFinished(true);

        Assertions.assertEquals(testRecord,compareRecord);
    }

    @Test
    public void AutomaticModePidLoopTest() throws IOException {
        AutomaticMode testClass = new AutomaticMode("testpath.csv", Constants.PID_CONTROL);
        Assertions.assertInstanceOf(Pid.class, testClass.getControlLoopCalculator());
    }

    @Test
    public void AutomaticModeLqrLoopTest() throws IOException {
        AutomaticMode testClass = new AutomaticMode("testpath.csv", Constants.LQR_CONTROL);
        Assertions.assertInstanceOf(Lqr.class, testClass.getControlLoopCalculator());
    }
}