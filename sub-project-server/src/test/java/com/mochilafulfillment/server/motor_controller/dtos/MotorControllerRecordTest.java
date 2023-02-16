package com.mochilafulfillment.server.motor_controller.dtos;

import com.mochilafulfillment.server.agv_utils.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MotorControllerRecordTest {
    @Test
    public void MotorControllerRecordTest(){
        MotorControllerRecord testRecord = new MotorControllerRecord();

        testRecord.setDirection(Constants.BACKWARDS_STRING);
        testRecord.setMotor1Velocity(Constants.STANDARD_VELOCITY);
        testRecord.setMotor2Velocity(Constants.STANDARD_VELOCITY);
        testRecord.setMotor1Sign(Constants.MOTOR_NEGATIVE);
        testRecord.setMotor2Sign(Constants.MOTOR_NEGATIVE);
        testRecord.setLiftType(Constants.LIFT_CONSTANT);
        testRecord.setNominalAccel(Constants.STANDARD_ACCELERATION);
        testRecord.setSafetyScannerMode(Constants.PICKING_SCAN);
        testRecord.setNewMotorControllerRecord(false);

        MotorControllerRecord testRecordStopped = testRecord.setStopped();

        MotorControllerRecord compareRecord = new MotorControllerRecord();
        compareRecord.setDirection(Constants.BACKWARDS_STRING);
        compareRecord.setMotor1Velocity(0);
        compareRecord.setMotor2Velocity(0);
        compareRecord.setMotor1Sign(Constants.MOTOR_POSITIVE);
        compareRecord.setMotor2Sign(Constants.MOTOR_POSITIVE);
        compareRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);
        compareRecord.setNominalAccel(Constants.STOPPING_ACCELERATION);
        compareRecord.setSafetyScannerMode(Constants.REGULAR_SCAN);
        compareRecord.setNewAgvToMotorControllerRecord(true);

        Assertions.assertEquals(testRecordStopped, compareRecord);

    }
}
