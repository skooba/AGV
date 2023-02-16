package com.mochilafulfillment.server.controls_logic;

import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.controls_logic.dtos.PathRecord;
import com.mochilafulfillment.server.controls_logic.dtos.RotationRecord;
import com.mochilafulfillment.server.controls_logic.utils.ControlLoop;
import com.mochilafulfillment.server.controls_logic.utils.LqrRotation;
import com.mochilafulfillment.server.dtos.AgvInputsRecord;
import com.mochilafulfillment.server.dtos.AgvOutputsRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class RotationTest {

    Rotation testClass;

    @BeforeEach
    public void init(){
        testClass = new Rotation();
        testClass.setRotationType(Constants.NO_CONTROL_ROTATION);
    }

    @Test
    public void toTargetCounterClockwiseTest(){
        ControlLoop mockedControlLoopCalculator = Mockito.mock(LqrRotation.class);
        Mockito.when(mockedControlLoopCalculator.iteration(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt())).thenReturn(new double[]{.55,0});
        testClass.setControlLoopCalculator(mockedControlLoopCalculator);

        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        AgvOutputsRecord testRecord2 = new AgvOutputsRecord();
        AgvOutputsRecord testRecord3 = new AgvOutputsRecord();
        AgvOutputsRecord testRecord4 = new AgvOutputsRecord();
        AgvOutputsRecord testRecord5 = new AgvOutputsRecord();
        AgvOutputsRecord testRecord6 = new AgvOutputsRecord();
        AgvOutputsRecord compareRecord = new AgvOutputsRecord();

        int angle = 180;
        int targetAngle = 90;

        int angle2 = -180;
        int targetAngle2 = 90;

        int angle3 = 0;
        int targetAngle3 = -90;

        int angle4 = -180;
        int targetAngle4 = -270;

        int angle5 = -181;
        int targetAngle5 = 0;

        int angle6 = -271;
        int targetAngle6 = -90;

        testRecord = testClass.toTarget(testRecord, angle, targetAngle, new AgvInputsRecord());
        testRecord2 = testClass.toTarget(testRecord2, angle2, targetAngle2, new AgvInputsRecord());
        testRecord3 = testClass.toTarget(testRecord3, angle3, targetAngle3, new AgvInputsRecord());
        testRecord4 = testClass.toTarget(testRecord4, angle4, targetAngle4, new AgvInputsRecord());
        testRecord5 = testClass.toTarget(testRecord5, angle5, targetAngle5, new AgvInputsRecord());
        testRecord6 = testClass.toTarget(testRecord6, angle6, targetAngle6, new AgvInputsRecord());


        compareRecord.setDirection(Constants.CCW_STRING);

        Assertions.assertEquals(testRecord, compareRecord);
        Assertions.assertEquals(testRecord2, compareRecord);
        Assertions.assertEquals(testRecord3, compareRecord);
        Assertions.assertEquals(testRecord4, compareRecord);
        Assertions.assertEquals(testRecord5, compareRecord);
        Assertions.assertEquals(testRecord6, compareRecord);

        Mockito.verify(mockedControlLoopCalculator, Mockito.never()).iteration(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt());


    }

    @Test
    public void toTargetLqrCounterClockwiseTest(){
        testClass.setRotationType(Constants.LQR_ROTATION);

        ControlLoop mockedControlLoopCalculator = Mockito.mock(LqrRotation.class);
        Mockito.when(mockedControlLoopCalculator.iteration(Mockito.anyDouble(),Mockito.anyDouble(),Mockito.anyDouble(),Mockito.anyInt())).thenReturn(new double[]{.55,0});
        testClass.setControlLoopCalculator(mockedControlLoopCalculator);

        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        AgvOutputsRecord testRecord2 = new AgvOutputsRecord();
        AgvOutputsRecord testRecord3 = new AgvOutputsRecord();
        AgvOutputsRecord testRecord4 = new AgvOutputsRecord();
        AgvOutputsRecord testRecord5 = new AgvOutputsRecord();
        AgvOutputsRecord testRecord6 = new AgvOutputsRecord();
        AgvOutputsRecord compareRecord = new AgvOutputsRecord();

        int angle = 180;
        int targetAngle = 90;

        int angle2 = -180;
        int targetAngle2 = 90;

        int angle3 = 0;
        int targetAngle3 = -90;

        int angle4 = -180;
        int targetAngle4 = -270;

        int angle5 = -181;
        int targetAngle5 = 0;

        int angle6 = -271;
        int targetAngle6 = -90;

        testRecord = testClass.toTarget(testRecord, angle, targetAngle, new AgvInputsRecord());
        testRecord2 = testClass.toTarget(testRecord2, angle2, targetAngle2, new AgvInputsRecord());
        testRecord3 = testClass.toTarget(testRecord3, angle3, targetAngle3, new AgvInputsRecord());
        testRecord4 = testClass.toTarget(testRecord4, angle4, targetAngle4, new AgvInputsRecord());
        testRecord5 = testClass.toTarget(testRecord5, angle5, targetAngle5, new AgvInputsRecord());
        testRecord6 = testClass.toTarget(testRecord6, angle6, targetAngle6, new AgvInputsRecord());


        compareRecord.setDirection(Constants.CCW_STRING);
        compareRecord.setMotorVelocityRatio(.55);

        Assertions.assertEquals(testRecord, compareRecord);
        Assertions.assertEquals(testRecord2, compareRecord);
        Assertions.assertEquals(testRecord3, compareRecord);
        Assertions.assertEquals(testRecord4, compareRecord);
        Assertions.assertEquals(testRecord5, compareRecord);
        Assertions.assertEquals(testRecord6, compareRecord);

    }

    @Test
    public void toTargetClockwiseTest(){
        ControlLoop mockedControlLoopCalculator = Mockito.mock(LqrRotation.class);
        Mockito.when(mockedControlLoopCalculator.iteration(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt())).thenReturn(new double[]{.55,0});
        testClass.setControlLoopCalculator(mockedControlLoopCalculator);

        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        AgvOutputsRecord testRecord2 = new AgvOutputsRecord();
        AgvOutputsRecord testRecord3 = new AgvOutputsRecord();
        AgvOutputsRecord testRecord4 = new AgvOutputsRecord();
        AgvOutputsRecord testRecord5 = new AgvOutputsRecord();
        AgvOutputsRecord testRecord6 = new AgvOutputsRecord();

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();

        int angle = 90;
        int targetAngle = 180;

        int angle2 = 90;
        int targetAngle2 = -180;

        int angle3 = -90;
        int targetAngle3 = 0;

        int angle4 = -270;
        int targetAngle4 = -180;

        int angle5 = -180;
        int targetAngle5 = 0;

        int angle6 = -270;
        int targetAngle6 = -90;

        testRecord = testClass.toTarget(testRecord, angle, targetAngle, new AgvInputsRecord());
        testRecord2 = testClass.toTarget(testRecord2, angle2, targetAngle2, new AgvInputsRecord());
        testRecord3 = testClass.toTarget(testRecord3, angle3, targetAngle3, new AgvInputsRecord());
        testRecord4 = testClass.toTarget(testRecord4, angle4, targetAngle4, new AgvInputsRecord());
        testRecord5 = testClass.toTarget(testRecord5, angle5, targetAngle5, new AgvInputsRecord());
        testRecord6 = testClass.toTarget(testRecord6, angle6, targetAngle6, new AgvInputsRecord());


        compareRecord.setDirection(Constants.CW_STRING);

        Assertions.assertEquals(testRecord, compareRecord);
        Assertions.assertEquals(testRecord2, compareRecord);
        Assertions.assertEquals(testRecord3, compareRecord);
        Assertions.assertEquals(testRecord4, compareRecord);
        Assertions.assertEquals(testRecord5, compareRecord);
        Assertions.assertEquals(testRecord6, compareRecord);

        Mockito.verify(mockedControlLoopCalculator, Mockito.never()).iteration(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt());


    }

    @Test
    public void toTargetLqrClockwiseTest(){
        testClass.setRotationType(Constants.LQR_ROTATION);

        ControlLoop mockedControlLoopCalculator = Mockito.mock(LqrRotation.class);
        Mockito.when(mockedControlLoopCalculator.iteration(Mockito.anyDouble(),Mockito.anyDouble(),Mockito.anyDouble(),Mockito.anyInt())).thenReturn(new double[]{.55,0});
        testClass.setControlLoopCalculator(mockedControlLoopCalculator);

        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        AgvOutputsRecord testRecord2 = new AgvOutputsRecord();
        AgvOutputsRecord testRecord3 = new AgvOutputsRecord();
        AgvOutputsRecord testRecord4 = new AgvOutputsRecord();
        AgvOutputsRecord testRecord5 = new AgvOutputsRecord();
        AgvOutputsRecord testRecord6 = new AgvOutputsRecord();

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();

        int angle = 90;
        int targetAngle = 180;

        int angle2 = 90;
        int targetAngle2 = -180;

        int angle3 = -90;
        int targetAngle3 = 0;

        int angle4 = -270;
        int targetAngle4 = -180;

        int angle5 = -180;
        int targetAngle5 = 0;

        int angle6 = -270;
        int targetAngle6 = -90;

        testRecord = testClass.toTarget(testRecord, angle, targetAngle, new AgvInputsRecord());
        testRecord2 = testClass.toTarget(testRecord2, angle2, targetAngle2, new AgvInputsRecord());
        testRecord3 = testClass.toTarget(testRecord3, angle3, targetAngle3, new AgvInputsRecord());
        testRecord4 = testClass.toTarget(testRecord4, angle4, targetAngle4, new AgvInputsRecord());
        testRecord5 = testClass.toTarget(testRecord5, angle5, targetAngle5, new AgvInputsRecord());
        testRecord6 = testClass.toTarget(testRecord6, angle6, targetAngle6, new AgvInputsRecord());


        compareRecord.setDirection(Constants.CW_STRING);
        compareRecord.setMotorVelocityRatio(.55);

        Assertions.assertEquals(testRecord, compareRecord);
        Assertions.assertEquals(testRecord2, compareRecord);
        Assertions.assertEquals(testRecord3, compareRecord);
        Assertions.assertEquals(testRecord4, compareRecord);
        Assertions.assertEquals(testRecord5, compareRecord);
        Assertions.assertEquals(testRecord6, compareRecord);

    }

    @Test
    public void runOnFinalAngleNotFinishedRunTest() throws InterruptedException {
        ControlLoop mockedControlLoopCalculator = Mockito.mock(LqrRotation.class);
        testClass.setControlLoopCalculator(mockedControlLoopCalculator);

        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setRotationIsFinished(false);

        RotationRecord rotationTestRecord = new RotationRecord();
        rotationTestRecord.setOnTargetAngleCounter(Constants.ON_TARGET_ANGLE_COUNTER -1);

        testRecord = testClass.runOnFinalAngle(testRecord, rotationTestRecord);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setRotationIsFinished(false);

        Assertions.assertEquals(testRecord, compareRecord);
        Mockito.verify(mockedControlLoopCalculator, Mockito.never()).resetTerms();
    }

    @Test
    public void runOnFinalAngleFinishedRunTest() throws InterruptedException {
        ControlLoop mockedControlLoopCalculator = Mockito.mock(LqrRotation.class);
        testClass.setControlLoopCalculator(mockedControlLoopCalculator);

        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setRotationIsFinished(false);
        testRecord.setMotorVelocityRatio(10);

        RotationRecord rotationTestRecord = new RotationRecord();
        rotationTestRecord.setOnTargetAngleCounter(Constants.ON_TARGET_ANGLE_COUNTER);

        testRecord = testClass.runOnFinalAngle(testRecord, rotationTestRecord);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setRotationIsFinished(true);
        compareRecord.setMotorVelocityRatio(0);

        Assertions.assertEquals(testRecord, compareRecord);
        Mockito.verify(mockedControlLoopCalculator).resetTerms();

    }

    @Test
    public void noRotationRequiredMainRunTest() throws InterruptedException {
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setRotationIsFinished(false);

        AgvOutputsRecord testRecord2 = new AgvOutputsRecord();
        testRecord2.setRotationIsFinished(false);

        AgvOutputsRecord testRecord3 = new AgvOutputsRecord();
        testRecord3.setRotationIsFinished(false);

        AgvInputsRecord testInputRecord = new AgvInputsRecord();
        testInputRecord.setTagAngle(90);

        AgvInputsRecord testInputRecord2 = new AgvInputsRecord();
        testInputRecord2.setTagAngle(90+Constants.FINAL_ANGLE_TOLERANCE);

        AgvInputsRecord testInputRecord3 = new AgvInputsRecord();
        testInputRecord3.setTagAngle(90-Constants.FINAL_ANGLE_TOLERANCE);

        PathRecord testPathRecord = new PathRecord(500, 90, 0);

        RotationRecord compareRotationRecord = new RotationRecord();

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setRotationIsFinished(true);

        testRecord = testClass.run(testRecord, testInputRecord, testPathRecord);
        Assertions.assertEquals(testClass.getRotationRecord(), compareRotationRecord);
        Assertions.assertEquals(testRecord, compareRecord);

        testClass = new Rotation();
        testRecord2 = testClass.run(testRecord2, testInputRecord2, testPathRecord);
        Assertions.assertEquals(testClass.getRotationRecord(), compareRotationRecord);
        Assertions.assertEquals(testRecord2, compareRecord);

        testClass = new Rotation();
        testRecord3 = testClass.run(testRecord3, testInputRecord3, testPathRecord);
        Assertions.assertEquals(testClass.getRotationRecord(), compareRotationRecord);
        Assertions.assertEquals(testRecord3, compareRecord);
    }

    @Test
    public void firstPassClockwiseMainRunTest() throws InterruptedException {
        AgvOutputsRecord testRecord = new AgvOutputsRecord();

        AgvOutputsRecord testRecord2 = new AgvOutputsRecord();

        AgvOutputsRecord testRecord3 = new AgvOutputsRecord();

        AgvInputsRecord testInputsRecord = new AgvInputsRecord();
        testInputsRecord.setTagAngle(90);

        AgvInputsRecord testInputsRecord2 = new AgvInputsRecord();
        testInputsRecord2.setTagAngle(-95);

        PathRecord testPathRecord = new PathRecord(50, 180, 0);
        PathRecord testPathRecord2 = new PathRecord(50, 270, 0);

        RotationRecord compareRotationRecord = new RotationRecord();
        compareRotationRecord.setStartedRotation(true);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setDirection(Constants.CW_STRING);

        testRecord = testClass.run(testRecord, testInputsRecord, testPathRecord);
        Assertions.assertEquals(testClass.getRotationRecord(), compareRotationRecord);
        Assertions.assertEquals(testRecord, compareRecord);

        testClass = new Rotation();
        testClass.setRotationType(Constants.NO_CONTROL_ROTATION);
        testRecord2 = testClass.run(testRecord2, testInputsRecord, testPathRecord2);
        Assertions.assertEquals(testClass.getRotationRecord(), compareRotationRecord);
        Assertions.assertEquals(testRecord2, compareRecord);

        testClass = new Rotation();
        testClass.setRotationType(Constants.NO_CONTROL_ROTATION);
        testRecord3 = testClass.run(testRecord3, testInputsRecord2, testPathRecord2);
        Assertions.assertEquals(testClass.getRotationRecord(), compareRotationRecord);
        Assertions.assertEquals(testRecord3, compareRecord);
    }

    @Test
    public void firstPassCounterClockwiseMainRunTest() throws InterruptedException {
        AgvOutputsRecord testRecord = new AgvOutputsRecord();

        AgvOutputsRecord testRecord2 = new AgvOutputsRecord();

        AgvOutputsRecord testRecord3 = new AgvOutputsRecord();

        AgvInputsRecord testInputsRecord = new AgvInputsRecord();
        testInputsRecord.setTagAngle(90);

        AgvInputsRecord testInputsRecord2 = new AgvInputsRecord();
        testInputsRecord2.setTagAngle(-105);

        PathRecord testPathRecord = new PathRecord(50, -90, 0);
        PathRecord testPathRecord2 = new PathRecord(50, 0, 0);
        PathRecord testPathRecord3 = new PathRecord(50, -180, 0);


        RotationRecord compareRotationRecord = new RotationRecord();
        compareRotationRecord.setStartedRotation(true);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setDirection(Constants.CCW_STRING);

        testRecord = testClass.run(testRecord, testInputsRecord, testPathRecord);
        Assertions.assertEquals(testClass.getRotationRecord(), compareRotationRecord);
        Assertions.assertEquals(testRecord, compareRecord);

        testClass = new Rotation();
        testClass.setRotationType(Constants.NO_CONTROL_ROTATION);
        testRecord2 = testClass.run(testRecord2, testInputsRecord, testPathRecord2);
        Assertions.assertEquals(testClass.getRotationRecord(), compareRotationRecord);
        Assertions.assertEquals(testRecord2, compareRecord);

        testClass = new Rotation();
        testClass.setRotationType(Constants.NO_CONTROL_ROTATION);
        testRecord3 = testClass.run(testRecord3, testInputsRecord2, testPathRecord3);
        Assertions.assertEquals(testClass.getRotationRecord(), compareRotationRecord);
        Assertions.assertEquals(testRecord3, compareRecord);


    }

    @Test
    public void notOnFinalAngleNotFirstPassClockwiseMainRunTest() throws InterruptedException {
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setRotationIsFinished(false);

        testClass.getRotationRecord().setStartedRotation(true);
        testClass.getRotationRecord().setOnTargetAngleCounter(Constants.ON_TARGET_ANGLE_COUNTER -1);

        AgvOutputsRecord testRecord2 = new AgvOutputsRecord();
        testRecord2.setRotationIsFinished(false);

        AgvOutputsRecord testRecord3 = new AgvOutputsRecord();
        testRecord3.setRotationIsFinished(false);

        AgvInputsRecord testInputsRecord = new AgvInputsRecord();
        testInputsRecord.setTagAngle(90);

        AgvInputsRecord testInputsRecord2 = new AgvInputsRecord();
        testInputsRecord2.setTagAngle(-95);

        PathRecord testPathRecord = new PathRecord(50, 180, 0);
        PathRecord testPathRecord2 = new PathRecord(50, 270, 0);

        RotationRecord compareRotationRecord = new RotationRecord();
        compareRotationRecord.setStartedRotation(true);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setDirection(Constants.CW_STRING);
        compareRecord.setRotationIsFinished(false);

        testRecord = testClass.run(testRecord, testInputsRecord, testPathRecord);
        Assertions.assertEquals(testClass.getRotationRecord(), compareRotationRecord);
        Assertions.assertEquals(testRecord, compareRecord);

        testClass = new Rotation();
        testClass.setRotationType(Constants.NO_CONTROL_ROTATION);
        testRecord2 = testClass.run(testRecord2, testInputsRecord, testPathRecord2);
        Assertions.assertEquals(testClass.getRotationRecord(), compareRotationRecord);
        Assertions.assertEquals(testRecord2, compareRecord);

        testClass = new Rotation();
        testClass.setRotationType(Constants.NO_CONTROL_ROTATION);
        testRecord3 = testClass.run(testRecord3, testInputsRecord2, testPathRecord2);
        Assertions.assertEquals(testClass.getRotationRecord(), compareRotationRecord);
        Assertions.assertEquals(testRecord3, compareRecord);


    }

    @Test
    public void notOnFinalAngleNotFirstPassCounterClockwiseMainRunTest() throws InterruptedException {
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setRotationIsFinished(false);

        testClass.getRotationRecord().setStartedRotation(true);
        testClass.getRotationRecord().setOnTargetAngleCounter(Constants.ON_TARGET_ANGLE_COUNTER -1);

        AgvOutputsRecord testRecord2 = new AgvOutputsRecord();
        testRecord2.setRotationIsFinished(false);

        AgvOutputsRecord testRecord3 = new AgvOutputsRecord();
        testRecord3.setRotationIsFinished(false);

        AgvInputsRecord testInputsRecord = new AgvInputsRecord();
        testInputsRecord.setTagAngle(90);

        AgvInputsRecord testInputsRecord2 = new AgvInputsRecord();
        testInputsRecord2.setTagAngle(-105);

        PathRecord testPathRecord = new PathRecord(50, -90, 0);
        PathRecord testPathRecord2 = new PathRecord(50, 0, 0);
        PathRecord testPathRecord3 = new PathRecord(50, -180, 0);

        RotationRecord compareRotationRecord = new RotationRecord();
        compareRotationRecord.setStartedRotation(true);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setDirection(Constants.CCW_STRING);
        compareRecord.setRotationIsFinished(false);

        testRecord = testClass.run(testRecord, testInputsRecord, testPathRecord);
        Assertions.assertEquals(testClass.getRotationRecord(), compareRotationRecord);
        Assertions.assertEquals(testRecord, compareRecord);

        testClass = new Rotation();
        testClass.setRotationType(Constants.NO_CONTROL_ROTATION);
        testRecord2 = testClass.run(testRecord2, testInputsRecord, testPathRecord2);
        Assertions.assertEquals(testClass.getRotationRecord(), compareRotationRecord);
        Assertions.assertEquals(testRecord2, compareRecord);

        testClass = new Rotation();
        testClass.setRotationType(Constants.NO_CONTROL_ROTATION);
        testRecord3 = testClass.run(testRecord3, testInputsRecord2, testPathRecord3);
        Assertions.assertEquals(testClass.getRotationRecord(), compareRotationRecord);
        Assertions.assertEquals(testRecord3, compareRecord);

    }

    @Test
    public void onFinalAngleNotFinishedMainRunTest() throws InterruptedException {
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setRotationIsFinished(false);
        testRecord.setDirection(Constants.CCW_STRING);

        AgvOutputsRecord testRecord2 = new AgvOutputsRecord();
        testRecord2.setRotationIsFinished(false);
        testRecord2.setDirection(Constants.CW_STRING);

        AgvOutputsRecord testRecord3 = new AgvOutputsRecord();
        testRecord3.setRotationIsFinished(false);
        testRecord3.setDirection(Constants.CW_STRING);

        testClass.getRotationRecord().setStartedRotation(true);
        testClass.getRotationRecord().setOnTargetAngleCounter(Constants.ON_TARGET_ANGLE_COUNTER -1);

        AgvInputsRecord testInputsRecord = new AgvInputsRecord();
        testInputsRecord.setTagAngle(90);

        AgvInputsRecord testInputsRecord2 = new AgvInputsRecord();
        testInputsRecord2.setTagAngle(-90-Constants.FINAL_ANGLE_TOLERANCE);

        AgvInputsRecord testInputsRecord3 = new AgvInputsRecord();
        testInputsRecord3.setTagAngle(270+Constants.FINAL_ANGLE_TOLERANCE);

        PathRecord testPathRecord = new PathRecord(50, 90, 0);
        PathRecord testPathRecord2 = new PathRecord(50, -90, 0);
        PathRecord testPathRecord3 = new PathRecord(50, 270, 0);

        RotationRecord compareRotationRecord = new RotationRecord();
        compareRotationRecord.setStartedRotation(true);
        compareRotationRecord.setOnTargetAngleCounter(Constants.ON_TARGET_ANGLE_COUNTER);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setDirection(Constants.CCW_STRING);
        compareRecord.setRotationIsFinished(false);

        AgvOutputsRecord compareRecord2 = new AgvOutputsRecord();
        compareRecord2.setDirection(Constants.CW_STRING);
        compareRecord2.setRotationIsFinished(false);

        testRecord = testClass.run(testRecord, testInputsRecord, testPathRecord);
        Assertions.assertEquals(testClass.getRotationRecord(), compareRotationRecord);
        Assertions.assertEquals(testRecord, compareRecord);

        testClass = new Rotation();
        testClass.setRotationType(Constants.NO_CONTROL_ROTATION);
        testClass.getRotationRecord().setStartedRotation(true);
        testClass.getRotationRecord().setOnTargetAngleCounter(Constants.ON_TARGET_ANGLE_COUNTER -1);
        testRecord2 = testClass.run(testRecord2, testInputsRecord2, testPathRecord2);
        Assertions.assertEquals(testClass.getRotationRecord(), compareRotationRecord);
        Assertions.assertEquals(testRecord2, compareRecord2);

        testClass = new Rotation();
        testClass.setRotationType(Constants.NO_CONTROL_ROTATION);
        testClass.getRotationRecord().setStartedRotation(true);
        testClass.getRotationRecord().setOnTargetAngleCounter(Constants.ON_TARGET_ANGLE_COUNTER -1);
        testRecord3 = testClass.run(testRecord3, testInputsRecord3, testPathRecord3);
        Assertions.assertEquals(testClass.getRotationRecord(), compareRotationRecord);
        Assertions.assertEquals(testRecord3, compareRecord2);

    }

    @Test
    public void onFinalAngleFinishedMainRunTest() throws InterruptedException {
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setRotationIsFinished(false);
        testRecord.setDirection(Constants.CCW_STRING);

        AgvOutputsRecord testRecord2 = new AgvOutputsRecord();
        testRecord2.setRotationIsFinished(false);
        testRecord2.setDirection(Constants.CW_STRING);

        AgvOutputsRecord testRecord3 = new AgvOutputsRecord();
        testRecord3.setRotationIsFinished(false);
        testRecord3.setDirection(Constants.CW_STRING);

        testClass.getRotationRecord().setStartedRotation(true);
        testClass.getRotationRecord().setOnTargetAngleCounter(Constants.ON_TARGET_ANGLE_COUNTER);

        AgvInputsRecord testInputsRecord = new AgvInputsRecord();
        testInputsRecord.setTagAngle(90);

        AgvInputsRecord testInputsRecord2 = new AgvInputsRecord();
        testInputsRecord2.setTagAngle(-90-Constants.FINAL_ANGLE_TOLERANCE);

        AgvInputsRecord testInputsRecord3 = new AgvInputsRecord();
        testInputsRecord3.setTagAngle(270+Constants.FINAL_ANGLE_TOLERANCE);

        PathRecord testPathRecord = new PathRecord(50, 90, 0);
        PathRecord testPathRecord2 = new PathRecord(50, -90, 0);
        PathRecord testPathRecord3 = new PathRecord(50, 270, 0);

        RotationRecord compareRotationRecord = new RotationRecord();
        compareRotationRecord.setStartedRotation(false);
        compareRotationRecord.setOnTargetAngleCounter(1);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setDirection(Constants.STOP_STRING);
        compareRecord.setRotationIsFinished(true);
        compareRecord.setMotorVelocityRatio(0);

        testRecord = testClass.run(testRecord, testInputsRecord, testPathRecord);
        Assertions.assertEquals(testClass.getRotationRecord(), compareRotationRecord);
        Assertions.assertEquals(testRecord, compareRecord);

        testClass.getRotationRecord().setStartedRotation(true);
        testClass.getRotationRecord().setOnTargetAngleCounter(Constants.ON_TARGET_ANGLE_COUNTER);
        testRecord2 = testClass.run(testRecord2, testInputsRecord2, testPathRecord2);
        Assertions.assertEquals(testClass.getRotationRecord(), compareRotationRecord);
        Assertions.assertEquals(testRecord2, compareRecord);

        testClass.getRotationRecord().setStartedRotation(true);
        testClass.getRotationRecord().setOnTargetAngleCounter(Constants.ON_TARGET_ANGLE_COUNTER);
        testRecord3 = testClass.run(testRecord3, testInputsRecord3, testPathRecord3);
        Assertions.assertEquals(testClass.getRotationRecord(), compareRotationRecord);
        Assertions.assertEquals(testRecord3, compareRecord);

    }


}