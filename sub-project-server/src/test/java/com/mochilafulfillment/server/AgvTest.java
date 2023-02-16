package com.mochilafulfillment.server;

import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.agv_utils.Exceptions.PGVException;
import com.mochilafulfillment.server.api.laptop.TerminalReader;
import com.mochilafulfillment.server.dtos.AgvOutputsRecord;
import com.mochilafulfillment.server.modes.AutomaticMode;
import com.mochilafulfillment.server.modes.Mode;
import com.mochilafulfillment.server.modes.RemoteControlMode;
import com.mochilafulfillment.server.motor_controller.AGVDrive;
import com.mochilafulfillment.server.motor_controller.dtos.MotorControllerRecord;
import com.mochilafulfillment.server.position_scanner.dtos.PositionScannerResponseRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import static org.mockito.Mockito.*;

public class AgvTest {

    Agv testClass;
    TerminalReader mockedTerminalReader;

    @BeforeEach
    public void init(){
        testClass = new Agv(new TerminalReader(),new MotorControllerRecord(),new PositionScannerResponseRecord(), new ReentrantLock());
        mockedTerminalReader = mock(TerminalReader.class);
    }

    @Test
    public void initialOperationRemoteModeTest() throws InterruptedException, IOException {
        when(mockedTerminalReader.getCurrentValue()).thenReturn("1");
        when(mockedTerminalReader.isEndProgram()).thenReturn(true);
        testClass.setTerminalReader(mockedTerminalReader);
        Mode modeObject = testClass.initialOperationMode();
        Assertions.assertEquals(modeObject.getClass(), RemoteControlMode.class);
    }

    @Test
    public void initialOperationAutomaticModeTest() throws InterruptedException, IOException {
        when(mockedTerminalReader.getCurrentValue()).thenReturn("2");
        when(mockedTerminalReader.isEndProgram()).thenReturn(true);
        testClass.setTerminalReader(mockedTerminalReader);
        Mode modeObject = testClass.initialOperationMode();
        Assertions.assertEquals(modeObject.getClass(), AutomaticMode.class);
    }

    @Test
    public void initialOperationIllegalFirstTryModeTest() throws InterruptedException, IOException {
        when(mockedTerminalReader.getCurrentValue()).thenReturn("3").thenReturn("2");
        when(mockedTerminalReader.isEndProgram()).thenReturn(true);
        testClass.setTerminalReader(mockedTerminalReader);
        Mode modeObject = testClass.initialOperationMode();
        Assertions.assertEquals(modeObject.getClass(), AutomaticMode.class);
    }

    @Test
    public void terminateProgramTest() {
        when(mockedTerminalReader.getCurrentValue()).thenReturn("2");
        when(mockedTerminalReader.isEndProgram()).thenReturn(true);
        testClass.setTerminalReader(mockedTerminalReader);

        MotorControllerRecord mockedControllerRecord = mock(MotorControllerRecord.class);
        testClass.setControllerRecord(mockedControllerRecord);

        testClass.run();

        verify(mockedControllerRecord, times(1)).setStopped();
        verify(mockedControllerRecord, never()).setNominalAccel(anyDouble());
        verify(mockedControllerRecord, never()).setDirection(any());
        verify(mockedControllerRecord, never()).setMotor1Velocity(anyDouble());
        verify(mockedControllerRecord, never()).setMotor2Velocity(anyDouble());
        verify(mockedControllerRecord, never()).setMotor1Sign(any());
        verify(mockedControllerRecord, never()).setMotor2Sign(any());
        verify(mockedControllerRecord, never()).setLiftType(anyInt());
        verify(mockedControllerRecord, never()).setSafetyScannerMode(anyInt());
        verify(mockedControllerRecord, never()).setNewAgvToMotorControllerRecord(anyBoolean());

    }

    @Test
    public void modeFinishedChangeModeToRemoteTest() throws IOException, InterruptedException, PGVException {
        Agv spyTestClass = spy(testClass);

        when(mockedTerminalReader.getCurrentValue()).thenReturn("2","1");
        when(mockedTerminalReader.isEndProgram()).thenReturn(false).thenReturn(true);
        spyTestClass.setTerminalReader(mockedTerminalReader);

        MotorControllerRecord mockedControllerRecord = mock(MotorControllerRecord.class);
        spyTestClass.setControllerRecord(mockedControllerRecord);


        AgvOutputsRecord testRecordAuto = new AgvOutputsRecord();
        testRecordAuto.setModeFinished(true);
        testRecordAuto.setDirection(Constants.FORWARDS_STRING);
        testRecordAuto.setMotor1Velocity(50);
        testRecordAuto.setMotor2Velocity(50);
        testRecordAuto.setNominalAccel(50.5);
        testRecordAuto.setMotor1Sign(Constants.MOTOR_NEGATIVE);
        testRecordAuto.setMotor2Sign(Constants.MOTOR_POSITIVE);
        testRecordAuto.setLiftType(Constants.LIFT_CONSTANT);

        AgvOutputsRecord testRecordRemote = new AgvOutputsRecord();
        testRecordRemote.setModeFinished(false);
        testRecordRemote.setDirection(Constants.FORWARDS_STRING);
        testRecordRemote.setMotor1Velocity(40);
        testRecordRemote.setMotor2Velocity(40);
        testRecordRemote.setNominalAccel(40.5);
        testRecordRemote.setMotor1Sign(Constants.MOTOR_NEGATIVE);
        testRecordRemote.setMotor2Sign(Constants.MOTOR_POSITIVE);
        testRecordRemote.setLiftType(Constants.LIFT_CONSTANT);

        RemoteControlMode mockedRemoteMode = mock(RemoteControlMode.class);
        doReturn(testRecordRemote).when(mockedRemoteMode).run(any(),any(),any(),any());
        AutomaticMode mockedAutoMode = mock(AutomaticMode.class);
        doReturn(testRecordAuto).when(mockedAutoMode).run(any(),any(),any(),any());
        doReturn(mockedRemoteMode).when(spyTestClass).changeModeToRemote();
        doReturn(mockedAutoMode).when(spyTestClass).changeModeToAuto();

        spyTestClass.run();

        verify(spyTestClass, times(2)).initialOperationMode();
        verify(mockedAutoMode, times(1)).run(any(),any(),any(),any());
        verify(mockedRemoteMode, times(2)).run(any(),any(),any(),any());
        verify(mockedControllerRecord, times(2)).setStopped();
        verify(mockedControllerRecord, times(1)).setNominalAccel(anyDouble());
        verify(mockedControllerRecord, times(1)).setDirection(any());
        verify(mockedControllerRecord, times(1)).setMotor1Velocity(anyDouble());
        verify(mockedControllerRecord, times(1)).setMotor2Velocity(anyDouble());
        verify(mockedControllerRecord, times(1)).setMotor1Sign(any());
        verify(mockedControllerRecord, times(1)).setMotor2Sign(any());
        verify(mockedControllerRecord, times(1)).setLiftType(anyInt());
        verify(mockedControllerRecord, times(1)).setSafetyScannerMode(anyInt());
        verify(mockedControllerRecord, times(1)).setNewAgvToMotorControllerRecord(anyBoolean());

    }

    @Test
    public void pathFinishedChangeModeToAutoTest() throws IOException, InterruptedException, PGVException {
        Agv spyTestClass = spy(testClass);

        when(mockedTerminalReader.getCurrentValue()).thenReturn("1","2");
        when(mockedTerminalReader.isEndProgram()).thenReturn(false).thenReturn(true);
        spyTestClass.setTerminalReader(mockedTerminalReader);

        MotorControllerRecord mockedControllerRecord = mock(MotorControllerRecord.class);
        spyTestClass.setControllerRecord(mockedControllerRecord);


        AgvOutputsRecord testRecordRemote = new AgvOutputsRecord();
        testRecordRemote.setPathFinished(true);
        testRecordRemote.setDirection(Constants.FORWARDS_STRING);
        testRecordRemote.setMotor1Velocity(50);
        testRecordRemote.setMotor2Velocity(50);
        testRecordRemote.setNominalAccel(50.5);
        testRecordRemote.setMotor1Sign(Constants.MOTOR_NEGATIVE);
        testRecordRemote.setMotor2Sign(Constants.MOTOR_POSITIVE);
        testRecordRemote.setLiftType(Constants.LIFT_CONSTANT);

        AgvOutputsRecord testRecordAuto = new AgvOutputsRecord();
        testRecordAuto.setPathFinished(false);
        testRecordAuto.setDirection(Constants.FORWARDS_STRING);
        testRecordAuto.setMotor1Velocity(40);
        testRecordAuto.setMotor2Velocity(40);
        testRecordAuto.setNominalAccel(40.5);
        testRecordAuto.setMotor1Sign(Constants.MOTOR_NEGATIVE);
        testRecordAuto.setMotor2Sign(Constants.MOTOR_POSITIVE);
        testRecordAuto.setLiftType(Constants.LIFT_CONSTANT);

        RemoteControlMode mockedRemoteMode = mock(RemoteControlMode.class);
        doReturn(testRecordRemote).when(mockedRemoteMode).run(any(),any(),any(),any());
        AutomaticMode mockedAutoMode = mock(AutomaticMode.class);
        doReturn(testRecordAuto).when(mockedAutoMode).run(any(),any(),any(),any());
        doReturn(mockedRemoteMode).when(spyTestClass).changeModeToRemote();
        doReturn(mockedAutoMode).when(spyTestClass).changeModeToAuto();

        spyTestClass.run();

        verify(spyTestClass, times(2)).initialOperationMode();
        verify(mockedAutoMode, times(2)).run(any(),any(),any(),any());
        verify(mockedRemoteMode, times(1)).run(any(),any(),any(),any());
        verify(mockedControllerRecord, times(2)).setStopped();
        verify(mockedControllerRecord, times(1)).setNominalAccel(anyDouble());
        verify(mockedControllerRecord, times(1)).setDirection(any());
        verify(mockedControllerRecord, times(1)).setMotor1Velocity(anyDouble());
        verify(mockedControllerRecord, times(1)).setMotor2Velocity(anyDouble());
        verify(mockedControllerRecord, times(1)).setMotor1Sign(any());
        verify(mockedControllerRecord, times(1)).setMotor2Sign(any());
        verify(mockedControllerRecord, times(1)).setLiftType(anyInt());
        verify(mockedControllerRecord, times(1)).setSafetyScannerMode(anyInt());
        verify(mockedControllerRecord, times(1)).setNewAgvToMotorControllerRecord(anyBoolean());
    }

    @Test
    public void updateControllerRecordTest() {

        when(mockedTerminalReader.getCurrentValue()).thenReturn("2");
        when(mockedTerminalReader.isExitAutoMode()).thenReturn(false);
        when(mockedTerminalReader.isEndProgram()).thenReturn(false).thenReturn(true);
        testClass.setTerminalReader(mockedTerminalReader);

        AgvOutputsRecord outputRecord = new AgvOutputsRecord();
        outputRecord.setDirection(Constants.FORWARDS_STRING);
        outputRecord.setNominalAccel(30);
        outputRecord.setNominalVelocity(60);
        outputRecord.setMotor1Velocity(60);
        outputRecord.setMotor2Velocity(60);
        outputRecord.setMotor1Sign(Constants.MOTOR_POSITIVE);
        outputRecord.setMotor2Sign(Constants.MOTOR_POSITIVE);
        outputRecord.setLiftType(Constants.LOWER_CONSTANT);
        outputRecord.setSafetyScannerMode(Constants.PICKING_SCAN);

        MotorControllerRecord testRecord = new MotorControllerRecord();
        MotorControllerRecord spyRecord = spy(testRecord);
        testClass.setControllerRecord(spyRecord);

        testClass.setAgvOutputs(outputRecord);
        testClass.run();

        verify(spyRecord).setNominalAccel(anyDouble());
        verify(spyRecord).setDirection(anyString());
        verify(spyRecord).setMotor1Velocity(anyDouble());
        verify(spyRecord).setMotor2Velocity(anyDouble());
        verify(spyRecord).setMotor1Sign(anyString());
        verify(spyRecord).setMotor2Sign(anyString());
        verify(spyRecord).setLiftType(anyInt());
        verify(spyRecord).setSafetyScannerMode(anyInt());
        verify(spyRecord).setNewAgvToMotorControllerRecord(anyBoolean());
        verify(spyRecord,times(1)).setStopped();

    }

    @Test
    public void doNotUpdateControllerRecordTest() {
        when(mockedTerminalReader.getCurrentValue()).thenReturn("2");
        when(mockedTerminalReader.isExitAutoMode()).thenReturn(false);
        when(mockedTerminalReader.isEndProgram()).thenReturn(false).thenReturn(true);
        testClass.setTerminalReader(mockedTerminalReader);

        AgvOutputsRecord outputRecord = new AgvOutputsRecord();
        testClass.setAgvOutputs(outputRecord);

        AGVDrive mockedAgvDrive = mock(AGVDrive.class);
        when(mockedAgvDrive.run(outputRecord)).thenReturn(new AgvOutputsRecord());

        testClass.setAgvDrive(mockedAgvDrive);

        MotorControllerRecord testRecord = new MotorControllerRecord();
        MotorControllerRecord spyRecord = spy(testRecord);
        testClass.setControllerRecord(spyRecord);

        testClass.run();

        verify(spyRecord,never()).setNominalAccel(anyDouble());
        verify(spyRecord,never()).setDirection(anyString());
        verify(spyRecord,never()).setMotor1Velocity(anyDouble());
        verify(spyRecord,never()).setMotor2Velocity(anyDouble());
        verify(spyRecord, never()).setMotor1Sign(anyString());
        verify(spyRecord, never()).setMotor2Sign(anyString());
        verify(spyRecord, never()).setLiftType(anyInt());
        verify(spyRecord, never()).setSafetyScannerMode(anyInt());
        verify(spyRecord,never()).setNewAgvToMotorControllerRecord(anyBoolean());
        verify(spyRecord,times(1)).setStopped();

    }

    @Test
    public void updateVelocityIncrementMotor1UpFirstPassTest(){
        when(mockedTerminalReader.getCurrentValue()).thenReturn("2");
        when(mockedTerminalReader.isExitAutoMode()).thenReturn(false);
        when(mockedTerminalReader.isEndProgram()).thenReturn(false).thenReturn(true);
        testClass.setTerminalReader(mockedTerminalReader);

        AgvOutputsRecord outputRecord = new AgvOutputsRecord();
        outputRecord.setMotor1Velocity(60);

        MotorControllerRecord testRecord = new MotorControllerRecord();
        MotorControllerRecord spyRecord = spy(testRecord);
        testClass.setControllerRecord(spyRecord);

        testClass.setAgvOutputs(outputRecord);
        testClass.run();

        double expectedVelocity = 0 + Constants.MAX_VELOCITY_CHANGE;

        verify(spyRecord).setMotor1Velocity(expectedVelocity);
    }

    @Test
    public void updateVelocityIncrementMotor1DownFirstPassTest() throws InterruptedException, PGVException {
        when(mockedTerminalReader.getCurrentValue()).thenReturn("2");
        when(mockedTerminalReader.isExitAutoMode()).thenReturn(false);
        when(mockedTerminalReader.isEndProgram()).thenReturn(false).thenReturn(true);
        testClass.setTerminalReader(mockedTerminalReader);


        AgvOutputsRecord outputRecord = new AgvOutputsRecord();
        outputRecord.setMotor1Velocity(-60);

        Mode modeObjectMock = mock(AutomaticMode.class);
        when(modeObjectMock.run(any(),any(),any(),any())).thenReturn(outputRecord);
        AGVDrive agvDriveMock = mock(AGVDrive.class);
        when(agvDriveMock.run(any())).thenReturn(outputRecord);


        MotorControllerRecord testRecord = new MotorControllerRecord();
        MotorControllerRecord spyRecord = spy(testRecord);
        testClass.setControllerRecord(spyRecord);

        testClass.setModeObject(modeObjectMock);
        testClass.setAgvDrive(agvDriveMock);
        testClass.setAgvOutputs(outputRecord);
        testClass.run();

        double expectedVelocity = 0 - Constants.MAX_VELOCITY_CHANGE;

        verify(spyRecord).setMotor1Velocity(expectedVelocity);
    }

    @Test
    public void updateVelocityIncrementMotor1NotFirstPassTest(){
        when(mockedTerminalReader.getCurrentValue()).thenReturn("2");
        when(mockedTerminalReader.isExitAutoMode()).thenReturn(false);
        when(mockedTerminalReader.isEndProgram()).thenReturn(false).thenReturn(true);
        testClass.setTerminalReader(mockedTerminalReader);
        testClass.setMotor1Counter(Constants.NUMBER_OF_LOOPS_BEFORE_CHANGE_VELOCITY);

        AgvOutputsRecord outputRecord = new AgvOutputsRecord();
        outputRecord.setMotor1Velocity(60);

        MotorControllerRecord testRecord = new MotorControllerRecord();
        MotorControllerRecord spyRecord = spy(testRecord);
        testClass.setControllerRecord(spyRecord);

        testClass.setAgvOutputs(outputRecord);
        testClass.run();

        double expectedVelocity = 0 + Constants.MAX_VELOCITY_CHANGE;

        verify(spyRecord).setMotor1Velocity(expectedVelocity);
    }

    @Test
    public void doNotIncrementVelocityMotor1Test(){
        when(mockedTerminalReader.getCurrentValue()).thenReturn("2");
        when(mockedTerminalReader.isExitAutoMode()).thenReturn(false);
        when(mockedTerminalReader.isEndProgram()).thenReturn(false).thenReturn(true);
        testClass.setTerminalReader(mockedTerminalReader);
        testClass.setMotor1Counter(Constants.NUMBER_OF_LOOPS_BEFORE_CHANGE_VELOCITY-1);

        AgvOutputsRecord outputRecord = new AgvOutputsRecord();
        outputRecord.setMotor1Velocity(60);

        MotorControllerRecord testRecord = new MotorControllerRecord();
        MotorControllerRecord spyRecord = spy(testRecord);
        testClass.setControllerRecord(spyRecord);

        testClass.setAgvOutputs(outputRecord);
        testClass.run();

        double expectedVelocity = 0 + Constants.MAX_VELOCITY_CHANGE;

        verify(spyRecord,never()).setMotor1Velocity(expectedVelocity);
    }

    @Test
    public void updateVelocityIncrementMotor1MaxVelocityChangeTest(){
        when(mockedTerminalReader.getCurrentValue()).thenReturn("2");
        when(mockedTerminalReader.isExitAutoMode()).thenReturn(false);
        when(mockedTerminalReader.isEndProgram()).thenReturn(false).thenReturn(true);
        testClass.setTerminalReader(mockedTerminalReader);

        AgvOutputsRecord outputRecord = new AgvOutputsRecord();
        outputRecord.setMotor1Velocity(Constants.MAX_VELOCITY_CHANGE);

        MotorControllerRecord testRecord = new MotorControllerRecord();
        MotorControllerRecord spyRecord = spy(testRecord);
        testClass.setControllerRecord(spyRecord);

        testClass.setAgvOutputs(outputRecord);
        testClass.run();

        double expectedVelocity = 0 + Constants.MAX_VELOCITY_CHANGE;

        verify(spyRecord).setMotor1Velocity(expectedVelocity);
    }

    @Test
    public void updateVelocityIncrementMotor2FirstPassTest(){
        when(mockedTerminalReader.getCurrentValue()).thenReturn("2");
        when(mockedTerminalReader.isExitAutoMode()).thenReturn(false);
        when(mockedTerminalReader.isEndProgram()).thenReturn(false).thenReturn(true);
        testClass.setTerminalReader(mockedTerminalReader);

        AgvOutputsRecord outputRecord = new AgvOutputsRecord();
        outputRecord.setMotor2Velocity(60);

        MotorControllerRecord testRecord = new MotorControllerRecord();
        MotorControllerRecord spyRecord = spy(testRecord);
        testClass.setControllerRecord(spyRecord);

        testClass.setAgvOutputs(outputRecord);
        testClass.run();

        double expectedVelocity = 0 + Constants.MAX_VELOCITY_CHANGE;

        verify(spyRecord).setMotor2Velocity(expectedVelocity);
    }

    @Test
    public void updateVelocityIncrementMotor2DownFirstPassTest() throws InterruptedException, PGVException {
        when(mockedTerminalReader.getCurrentValue()).thenReturn("2");
        when(mockedTerminalReader.isExitAutoMode()).thenReturn(false);
        when(mockedTerminalReader.isEndProgram()).thenReturn(false).thenReturn(true);
        testClass.setTerminalReader(mockedTerminalReader);


        AgvOutputsRecord outputRecord = new AgvOutputsRecord();
        outputRecord.setMotor2Velocity(-60);

        Mode modeObjectMock = mock(AutomaticMode.class);
        when(modeObjectMock.run(any(),any(),any(),any())).thenReturn(outputRecord);
        AGVDrive agvDriveMock = mock(AGVDrive.class);
        when(agvDriveMock.run(any())).thenReturn(outputRecord);


        MotorControllerRecord testRecord = new MotorControllerRecord();
        MotorControllerRecord spyRecord = spy(testRecord);
        testClass.setControllerRecord(spyRecord);

        testClass.setModeObject(modeObjectMock);
        testClass.setAgvDrive(agvDriveMock);
        testClass.setAgvOutputs(outputRecord);
        testClass.run();

        double expectedVelocity = 0 - Constants.MAX_VELOCITY_CHANGE;

        verify(spyRecord).setMotor2Velocity(expectedVelocity);
    }

    @Test
    public void updateVelocityIncrementMotor2NotFirstPassTest(){
        when(mockedTerminalReader.getCurrentValue()).thenReturn("2");
        when(mockedTerminalReader.isExitAutoMode()).thenReturn(false);
        when(mockedTerminalReader.isEndProgram()).thenReturn(false).thenReturn(true);
        testClass.setTerminalReader(mockedTerminalReader);
        testClass.setMotor2Counter(Constants.NUMBER_OF_LOOPS_BEFORE_CHANGE_VELOCITY);

        AgvOutputsRecord outputRecord = new AgvOutputsRecord();
        outputRecord.setMotor2Velocity(60);
        AGVDrive mockedAgvDrive = mock(AGVDrive.class);
        when(mockedAgvDrive.run(outputRecord)).thenReturn(outputRecord);

        testClass.setAgvDrive(mockedAgvDrive);

        MotorControllerRecord testRecord = new MotorControllerRecord();
        MotorControllerRecord spyRecord = spy(testRecord);
        testClass.setControllerRecord(spyRecord);

        testClass.setAgvOutputs(outputRecord);
        testClass.run();

        double expectedVelocity = 0 + Constants.MAX_VELOCITY_CHANGE;

        verify(spyRecord).setMotor2Velocity(expectedVelocity);
    }


    @Test
    public void doNotIncrementVelocityMotor2Test(){
        when(mockedTerminalReader.getCurrentValue()).thenReturn("2");
        when(mockedTerminalReader.isExitAutoMode()).thenReturn(false);
        when(mockedTerminalReader.isEndProgram()).thenReturn(false).thenReturn(true);
        testClass.setTerminalReader(mockedTerminalReader);
        testClass.setMotor2Counter(Constants.NUMBER_OF_LOOPS_BEFORE_CHANGE_VELOCITY-1);

        AgvOutputsRecord outputRecord = new AgvOutputsRecord();
        outputRecord.setMotor2Velocity(60);

        MotorControllerRecord testRecord = new MotorControllerRecord();
        MotorControllerRecord spyRecord = spy(testRecord);
        testClass.setControllerRecord(spyRecord);

        testClass.setAgvOutputs(outputRecord);
        testClass.run();

        double expectedVelocity = 0 + Constants.MAX_VELOCITY_CHANGE;

        verify(spyRecord,never()).setMotor2Velocity(expectedVelocity);
    }

    @Test
    public void updateVelocityIncrementMotor2MaxVelocityChangeTest(){
        when(mockedTerminalReader.getCurrentValue()).thenReturn("2");
        when(mockedTerminalReader.isExitAutoMode()).thenReturn(false);
        when(mockedTerminalReader.isEndProgram()).thenReturn(false).thenReturn(true);
        testClass.setTerminalReader(mockedTerminalReader);

        AgvOutputsRecord outputRecord = new AgvOutputsRecord();
        outputRecord.setMotor2Velocity(Constants.MAX_VELOCITY_CHANGE);

        MotorControllerRecord testRecord = new MotorControllerRecord();
        MotorControllerRecord spyRecord = spy(testRecord);
        testClass.setControllerRecord(spyRecord);

        testClass.setAgvOutputs(outputRecord);
        testClass.run();

        double expectedVelocity = 0 + Constants.MAX_VELOCITY_CHANGE;

        verify(spyRecord).setMotor2Velocity(expectedVelocity);
    }

    @Test
    public void agvFullyStoppedTest(){
        when(mockedTerminalReader.getCurrentValue()).thenReturn("2");
        when(mockedTerminalReader.isExitAutoMode()).thenReturn(false);
        when(mockedTerminalReader.isEndProgram()).thenReturn(false).thenReturn(true);
        testClass.setTerminalReader(mockedTerminalReader);
        testClass.setMotor1Counter(0);

        AgvOutputsRecord outputRecord = new AgvOutputsRecord();
        outputRecord.setMotor1Velocity(0);
        outputRecord.setMotor2Velocity(0);


        AGVDrive mockedAgvDrive = mock(AGVDrive.class);
        when(mockedAgvDrive.run(any())).thenReturn(outputRecord);
        testClass.setAgvDrive(mockedAgvDrive);

        testClass.run();
        AgvOutputsRecord testRecord = testClass.getAgvOutputs();

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setAgvStopped(true);

        Assertions.assertEquals(testRecord.isAgvStopped(), true);
    }
}