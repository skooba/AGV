package com.mochilafulfillment.server.controls_logic;

import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.controls_logic.dtos.PathRecord;
import com.mochilafulfillment.server.dtos.AgvInputsRecord;
import com.mochilafulfillment.server.dtos.AgvOutputsRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ForkLiftTest {

    ForkLift testClass = new ForkLift();

    @Test
    public void liftStartTest() throws InterruptedException {
        AgvOutputsRecord outputTestRecord = new AgvOutputsRecord();
        AgvOutputsRecord equalityRecord = new AgvOutputsRecord();
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();

        inputTestRecord.setTopSensorTriggered(true);
        outputTestRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);


        outputTestRecord = testClass.lift(outputTestRecord, inputTestRecord);

        equalityRecord.setLiftType(Constants.LIFT_CONSTANT);


        Assertions.assertEquals(outputTestRecord, equalityRecord);
    }

    @Test
    public void liftContinue() throws InterruptedException {
        AgvOutputsRecord outputTestRecord = new AgvOutputsRecord();
        AgvOutputsRecord equalityRecord = new AgvOutputsRecord();
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();

        outputTestRecord.setLiftType(Constants.LIFT_CONSTANT);

        outputTestRecord = testClass.lift(outputTestRecord, inputTestRecord);

        equalityRecord.setLiftType(Constants.LIFT_CONSTANT);

        Assertions.assertEquals(outputTestRecord, equalityRecord);
    }

    @Test
    public void liftStopTest() throws InterruptedException {
        AgvOutputsRecord outputTestRecord = new AgvOutputsRecord();
        AgvOutputsRecord equalityRecord = new AgvOutputsRecord();
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();

        inputTestRecord.setMiddleSensorTriggered(true);
        outputTestRecord.setLiftType(Constants.LIFT_CONSTANT);

        outputTestRecord = testClass.lift(outputTestRecord, inputTestRecord);

        equalityRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);
        equalityRecord.setForkLiftIsFinished(true);

        Assertions.assertEquals(outputTestRecord, equalityRecord);
    }

    @Test
    public void liftErrorTest() throws InterruptedException {
        AgvOutputsRecord outputTestRecord = new AgvOutputsRecord();
        AgvOutputsRecord equalityRecord = new AgvOutputsRecord();
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();

        inputTestRecord.setTopSensorTriggered(true);
        outputTestRecord.setLiftType(Constants.LIFT_CONSTANT);

        outputTestRecord = testClass.lift(outputTestRecord, inputTestRecord);

        equalityRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);

        Assertions.assertEquals(outputTestRecord, equalityRecord);
    }

    @Test
    public void pickStartTest() throws InterruptedException {
        AgvOutputsRecord outputTestRecord = new AgvOutputsRecord();
        AgvOutputsRecord equalityRecord = new AgvOutputsRecord();
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();

        inputTestRecord.setMiddleSensorTriggered(true);
        outputTestRecord.setLiftType(Constants.LIFT_CONSTANT);

        outputTestRecord = testClass.pick(outputTestRecord, inputTestRecord);

        equalityRecord.setLiftType(Constants.LIFT_CONSTANT);

        Assertions.assertEquals(outputTestRecord, equalityRecord);
    }

    @Test
    public void pickContinue() throws InterruptedException {
        AgvOutputsRecord outputTestRecord = new AgvOutputsRecord();
        AgvOutputsRecord equalityRecord = new AgvOutputsRecord();
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();

        outputTestRecord.setLiftType(Constants.LIFT_CONSTANT);

        outputTestRecord = testClass.pick(outputTestRecord, inputTestRecord);

        equalityRecord.setLiftType(Constants.LIFT_CONSTANT);

        Assertions.assertEquals(outputTestRecord, equalityRecord);
    }

    @Test
    public void pickStopTest() throws InterruptedException {
        AgvOutputsRecord outputTestRecord = new AgvOutputsRecord();
        AgvOutputsRecord equalityRecord = new AgvOutputsRecord();
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();

        inputTestRecord.setTopSensorTriggered(true);
        outputTestRecord.setLiftType(Constants.LIFT_CONSTANT);

        outputTestRecord = testClass.pick(outputTestRecord, inputTestRecord);

        equalityRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);
        equalityRecord.setForkLiftIsFinished(true);

        Assertions.assertEquals(outputTestRecord, equalityRecord);
    }

    @Test
    public void pickErrorTest() throws InterruptedException {
        AgvOutputsRecord outputTestRecord = new AgvOutputsRecord();
        AgvOutputsRecord equalityRecord = new AgvOutputsRecord();
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();

        inputTestRecord.setBottomSensorTriggered(true);
        outputTestRecord.setLiftType(Constants.LIFT_CONSTANT);

        outputTestRecord = testClass.pick(outputTestRecord, inputTestRecord);

        equalityRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);

        Assertions.assertEquals(outputTestRecord, equalityRecord);
    }


    @Test
    public void lowerStartTest() throws InterruptedException {
        AgvOutputsRecord outputTestRecord = new AgvOutputsRecord();
        AgvOutputsRecord equalityRecord = new AgvOutputsRecord();
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();

        inputTestRecord.setBottomSensorTriggered(true);
        outputTestRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);

        outputTestRecord = testClass.lower(outputTestRecord, inputTestRecord);

        equalityRecord.setLiftType(Constants.LOWER_CONSTANT);

        Assertions.assertEquals(outputTestRecord, equalityRecord);

        AgvOutputsRecord outputTestRecord2 = new AgvOutputsRecord();
        AgvInputsRecord inputTestRecord2 = new AgvInputsRecord();

        inputTestRecord2.setMiddleSensorTriggered(true);

        outputTestRecord2 = testClass.lower(outputTestRecord2, inputTestRecord2);

        equalityRecord.setLiftType(Constants.LOWER_CONSTANT);

        Assertions.assertEquals(outputTestRecord2, equalityRecord);
    }

    @Test
    public void lowerContinue() throws InterruptedException {
        AgvOutputsRecord outputTestRecord = new AgvOutputsRecord();
        AgvOutputsRecord equalityRecord = new AgvOutputsRecord();
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();

        outputTestRecord.setLiftType(Constants.LOWER_CONSTANT);

        outputTestRecord = testClass.lower(outputTestRecord, inputTestRecord);

        equalityRecord.setLiftType(Constants.LOWER_CONSTANT);

        Assertions.assertEquals(outputTestRecord, equalityRecord);
    }

    @Test
    public void lowerStopTest() throws InterruptedException {
        AgvOutputsRecord outputTestRecord = new AgvOutputsRecord();
        AgvOutputsRecord equalityRecord = new AgvOutputsRecord();
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();

        inputTestRecord.setBottomSensorTriggered(true);
        outputTestRecord.setLiftType(Constants.LOWER_CONSTANT);

        outputTestRecord = testClass.lower(outputTestRecord, inputTestRecord);

        equalityRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);
        equalityRecord.setForkLiftIsFinished(true);

        Assertions.assertEquals(outputTestRecord, equalityRecord);
    }

    @Test
    public void lowerErrorTest() throws InterruptedException {
        AgvOutputsRecord outputTestRecord = new AgvOutputsRecord();
        AgvOutputsRecord equalityRecord = new AgvOutputsRecord();
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();

        inputTestRecord.setBottomSensorTriggered(true);
        outputTestRecord.setLiftType(Constants.LOWER_CONSTANT);

        outputTestRecord = testClass.lower(outputTestRecord, inputTestRecord);

        equalityRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);

        Assertions.assertEquals(outputTestRecord, equalityRecord);
    }

    @Test
    public void placeStartTest() throws InterruptedException {
        AgvOutputsRecord outputTestRecord = new AgvOutputsRecord();
        AgvOutputsRecord equalityRecord = new AgvOutputsRecord();
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();

        inputTestRecord.setBottomSensorTriggered(true);
        outputTestRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);

        outputTestRecord = testClass.place(outputTestRecord, inputTestRecord);

        equalityRecord.setLiftType(Constants.LOWER_CONSTANT);

        Assertions.assertEquals(outputTestRecord, equalityRecord);
    }

    @Test
    public void placeContinue() throws InterruptedException {
        AgvOutputsRecord outputTestRecord = new AgvOutputsRecord();
        AgvOutputsRecord equalityRecord = new AgvOutputsRecord();
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();

        outputTestRecord.setLiftType(Constants.LOWER_CONSTANT);

        outputTestRecord = testClass.place(outputTestRecord, inputTestRecord);

        equalityRecord.setLiftType(Constants.LOWER_CONSTANT);

        Assertions.assertEquals(outputTestRecord, equalityRecord);
    }

    @Test
    public void placeStopTest() throws InterruptedException {
        AgvOutputsRecord outputTestRecord = new AgvOutputsRecord();
        AgvOutputsRecord equalityRecord = new AgvOutputsRecord();
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();

        inputTestRecord.setMiddleSensorTriggered(true);
        outputTestRecord.setLiftType(Constants.LOWER_CONSTANT);

        outputTestRecord = testClass.place(outputTestRecord, inputTestRecord);

        equalityRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);
        equalityRecord.setForkLiftIsFinished(true);

        Assertions.assertEquals(outputTestRecord, equalityRecord);
    }

    @Test
    public void placeErrorTest() throws InterruptedException {
        AgvOutputsRecord outputTestRecord = new AgvOutputsRecord();
        AgvOutputsRecord equalityRecord = new AgvOutputsRecord();
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();

        inputTestRecord.setTopSensorTriggered(true);
        outputTestRecord.setLiftType(Constants.LOWER_CONSTANT);

        outputTestRecord = testClass.place(outputTestRecord, inputTestRecord);

        equalityRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);

        Assertions.assertEquals(outputTestRecord, equalityRecord);
    }

    @Test
    public void runLiftTest() throws InterruptedException {
        AgvOutputsRecord outputTestRecord = new AgvOutputsRecord();
        AgvOutputsRecord equalityRecord = new AgvOutputsRecord();
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();
        PathRecord testPath = new PathRecord(50, 0, Constants.LIFT_LINE);

        inputTestRecord.setTopSensorTriggered(true);
        outputTestRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);

        outputTestRecord = testClass.run(outputTestRecord, inputTestRecord, testPath);

        equalityRecord.setLiftType(Constants.LIFT_CONSTANT);

        Assertions.assertEquals(outputTestRecord, equalityRecord);
    }

    @Test
    public void runPickTest() throws InterruptedException {
        AgvOutputsRecord outputTestRecord = new AgvOutputsRecord();
        AgvOutputsRecord equalityRecord = new AgvOutputsRecord();
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();
        PathRecord testPath = new PathRecord(50, 0, Constants.PICK_LINE);

        inputTestRecord.setMiddleSensorTriggered(true);
        outputTestRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);

        outputTestRecord = testClass.run(outputTestRecord, inputTestRecord, testPath);

        equalityRecord.setLiftType(Constants.LIFT_CONSTANT);

        Assertions.assertEquals(outputTestRecord, equalityRecord);

        AgvOutputsRecord outputTestRecord2 = new AgvOutputsRecord();
        AgvInputsRecord inputTestRecord2 = new AgvInputsRecord();

        inputTestRecord2.setTopSensorTriggered(true);
        outputTestRecord2.setLiftType(Constants.STOP_VERTICAL_CONSTANT);

        outputTestRecord2 = testClass.run(outputTestRecord2, inputTestRecord2, testPath);
        Assertions.assertEquals(outputTestRecord2, equalityRecord);

    }

    @Test
    public void runLowerTest() throws InterruptedException {
        AgvOutputsRecord outputTestRecord = new AgvOutputsRecord();
        AgvOutputsRecord equalityRecord = new AgvOutputsRecord();
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();
        PathRecord testPath = new PathRecord(50, 0, Constants.LOWER_LINE);

        inputTestRecord.setBottomSensorTriggered(true);
        outputTestRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);

        outputTestRecord = testClass.run(outputTestRecord, inputTestRecord, testPath);

        equalityRecord.setLiftType(Constants.LOWER_CONSTANT);

        Assertions.assertEquals(outputTestRecord, equalityRecord);
    }

    @Test
    public void runPlaceTest() throws InterruptedException {
        AgvOutputsRecord outputTestRecord = new AgvOutputsRecord();
        AgvOutputsRecord equalityRecord = new AgvOutputsRecord();
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();
        PathRecord testPath = new PathRecord(50, 0, Constants.PLACE_LINE);

        inputTestRecord.setBottomSensorTriggered(true);
        outputTestRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);

        outputTestRecord = testClass.run(outputTestRecord, inputTestRecord, testPath);

        equalityRecord.setLiftType(Constants.LOWER_CONSTANT);

        Assertions.assertEquals(outputTestRecord, equalityRecord);
    }
}
