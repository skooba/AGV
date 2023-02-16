package com.mochilafulfillment.server.dtos;

import com.mochilafulfillment.server.agv_utils.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AgvOutputsRecordTest {
    @Test
    public void agvOutputRecordCopyConstructorTest() throws InterruptedException {
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setRotationIsFinished(false);
        testRecord.setTagIsFinished(false);
        testRecord.setForkLiftIsFinished(false);
        testRecord.setLiftType(Constants.LOWER_CONSTANT);
        testRecord.setDirection(Constants.CW_STRING);
        testRecord.setLock(Constants.FORK_LIFT_KEY);
        testRecord.setNominalAccel(50d);
//        testRecord.setChangeMode(Constants.REMOTE_CONTROL_MODE);
        testRecord.setNominalVelocity(Constants.ON_STOP_TAG_VELOCITY);
        testRecord.setMotor1Sign(Constants.MOTOR_NEGATIVE);
        testRecord.setMotor2Sign(Constants.MOTOR_NEGATIVE);
        testRecord.setSafetyScannerMode(Constants.PICKING_SCAN);
        testRecord.setMotor1Velocity(Constants.STANDARD_VELOCITY);
        testRecord.setMotor2Velocity(Constants.STANDARD_VELOCITY);
        testRecord.setMotorVelocityRatio(.8);

        AgvOutputsRecord copyTestRecord = new AgvOutputsRecord(testRecord);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setLiftType(Constants.LOWER_CONSTANT);
        compareRecord.setDirection(Constants.CW_STRING);
        compareRecord.setNominalAccel(50d);
        compareRecord.setMotor1Sign(Constants.MOTOR_NEGATIVE);
        compareRecord.setMotor2Sign(Constants.MOTOR_NEGATIVE);
        compareRecord.setSafetyScannerMode(Constants.PICKING_SCAN);
        compareRecord.setMotor1Velocity(Constants.STANDARD_VELOCITY);
        compareRecord.setMotor2Velocity(Constants.STANDARD_VELOCITY);

        Assertions.assertEquals(copyTestRecord, compareRecord);
        Assertions.assertNotEquals(testRecord.hashCode(), copyTestRecord.hashCode());

    }
}
