package com.mochilafulfillment.server.motor_controller;

import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.dtos.AgvOutputsRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AGVDriveTest {

    AGVDrive testClass;
    AgvOutputsRecord testRecord;
    AgvOutputsRecord compareRecord;

    @BeforeEach
    public void init(){
        testRecord = new AgvOutputsRecord();
        compareRecord = new AgvOutputsRecord();
        testClass = new AGVDrive();
    }

    @Test
    public void setStoppedTest(){
        testRecord.setDirection(Constants.STOP_STRING);
        testRecord = testClass.run(testRecord);
        compareRecord.setMotor1Velocity(0);
        compareRecord.setMotor2Velocity(0);
        compareRecord.setDirection(Constants.STOP_STRING);
        compareRecord.setNominalAccel(Constants.STOPPING_ACCELERATION);
        Assertions.assertEquals(testRecord,compareRecord);
    }

    @Test
    public void setClockwiseDefaultTest(){
        testRecord.setDirection(Constants.CW_STRING);
        testRecord = testClass.run(testRecord);
        compareRecord.setDirection(Constants.CW_STRING);
        compareRecord.setMotor1Velocity(Constants.ROTATE_SPEED);
        compareRecord.setMotor2Velocity(Constants.ROTATE_SPEED);
        compareRecord.setMotor1Sign(Constants.MOTOR_NEGATIVE);
        compareRecord.setMotor2Sign(Constants.MOTOR_POSITIVE);
        compareRecord.setNominalAccel(Constants.ROTATE_ACCEL);
        Assertions.assertEquals(testRecord,compareRecord);
    }

    @Test
    public void setCounterClockwiseDefaultTest(){
        testRecord.setDirection(Constants.CCW_STRING);
        testRecord = testClass.run(testRecord);
        compareRecord.setDirection(Constants.CCW_STRING);
        compareRecord.setMotor1Velocity(Constants.ROTATE_SPEED);
        compareRecord.setMotor2Velocity(Constants.ROTATE_SPEED);
        compareRecord.setMotor1Sign(Constants.MOTOR_POSITIVE);
        compareRecord.setMotor2Sign(Constants.MOTOR_NEGATIVE);
        compareRecord.setNominalAccel(Constants.ROTATE_ACCEL);
        Assertions.assertEquals(testRecord,compareRecord);
    }

    @Test
    public void setClockwiseCustomTest(){
        testRecord.setDirection(Constants.CW_STRING);
        testRecord.setRotationVelocity(77);
        testRecord.setRotationAcceleration(89);
        testRecord = testClass.run(testRecord);
        compareRecord.setDirection(Constants.CW_STRING);
        compareRecord.setRotationVelocity(77);
        compareRecord.setRotationAcceleration(89);
        compareRecord.setMotor1Velocity(77);
        compareRecord.setMotor2Velocity(77);
        compareRecord.setMotor1Sign(Constants.MOTOR_NEGATIVE);
        compareRecord.setMotor2Sign(Constants.MOTOR_POSITIVE);
        compareRecord.setNominalAccel(89);
        Assertions.assertEquals(testRecord,compareRecord);
    }

    @Test
    public void setCounterClockwiseCustomTest(){
        testRecord.setDirection(Constants.CCW_STRING);
        testRecord.setRotationVelocity(87);
        testRecord.setRotationAcceleration(79);
        testRecord = testClass.run(testRecord);
        compareRecord.setDirection(Constants.CCW_STRING);
        compareRecord.setRotationVelocity(87);
        compareRecord.setRotationAcceleration(79);
        compareRecord.setMotor1Velocity(87);
        compareRecord.setMotor2Velocity(87);
        compareRecord.setMotor1Sign(Constants.MOTOR_POSITIVE);
        compareRecord.setMotor2Sign(Constants.MOTOR_NEGATIVE);
        compareRecord.setNominalAccel(79);
        Assertions.assertEquals(testRecord,compareRecord);
    }

    @Test
    public void setCounterClockwiseRatioGreaterThan1Test(){
        testRecord.setDirection(Constants.CCW_STRING);
        testRecord.setRotationVelocity(50);
        testRecord.setRotationAcceleration(100);
        testRecord.setMotorVelocityRatio(2);
        testRecord = testClass.run(testRecord);
        compareRecord.setDirection(Constants.CCW_STRING);
        compareRecord.setRotationVelocity(50);
        compareRecord.setRotationAcceleration(100);
        compareRecord.setMotor1Velocity(25);
        compareRecord.setMotor2Velocity(50);
        compareRecord.setMotor1Sign(Constants.MOTOR_POSITIVE);
        compareRecord.setMotor2Sign(Constants.MOTOR_POSITIVE);
        compareRecord.setNominalAccel(100);
        compareRecord.setMotorVelocityRatio(2);
        Assertions.assertEquals(testRecord,compareRecord);

    }

    @Test
    public void setCounterClockwiseRatioGreaterThan1BackwardTest(){
        testRecord.setDirection(Constants.CCW_STRING);
        testRecord.setRotationVelocity(50);
        testRecord.setRotationAcceleration(100);
        testRecord.setMotorVelocityRatio(2);
        testRecord.setBothMotorsNegativeForRotation(true);
        testRecord = testClass.run(testRecord);
        compareRecord.setDirection(Constants.CCW_STRING);
        compareRecord.setRotationVelocity(50);
        compareRecord.setRotationAcceleration(100);
        compareRecord.setMotor1Velocity(25);
        compareRecord.setMotor2Velocity(50);
        compareRecord.setMotor1Sign(Constants.MOTOR_NEGATIVE);
        compareRecord.setMotor2Sign(Constants.MOTOR_NEGATIVE);
        compareRecord.setNominalAccel(100);
        compareRecord.setMotorVelocityRatio(2);
        Assertions.assertEquals(testRecord,compareRecord);

    }

    @Test
    public void setCounterClockwiseRatioLessThanNegative1Test(){
        testRecord.setDirection(Constants.CCW_STRING);
        testRecord.setRotationVelocity(50);
        testRecord.setRotationAcceleration(100);
        testRecord.setMotorVelocityRatio(-2);
        testRecord = testClass.run(testRecord);
        compareRecord.setDirection(Constants.CCW_STRING);
        compareRecord.setRotationVelocity(50);
        compareRecord.setRotationAcceleration(100);
        compareRecord.setMotor1Velocity(25);
        compareRecord.setMotor2Velocity(50);
        compareRecord.setMotor1Sign(Constants.MOTOR_POSITIVE);
        compareRecord.setMotor2Sign(Constants.MOTOR_NEGATIVE);
        compareRecord.setNominalAccel(100);
        compareRecord.setMotorVelocityRatio(-2);
        Assertions.assertEquals(testRecord,compareRecord);
    }

    @Test
    public void setCounterClockwiseRatioLessThan1PositiveTest(){
        testRecord.setDirection(Constants.CCW_STRING);
        testRecord.setRotationVelocity(50);
        testRecord.setRotationAcceleration(100);
        testRecord.setMotorVelocityRatio(.5);
        testRecord = testClass.run(testRecord);
        compareRecord.setDirection(Constants.CCW_STRING);
        compareRecord.setRotationVelocity(50);
        compareRecord.setRotationAcceleration(100);
        compareRecord.setMotor1Velocity(50);
        compareRecord.setMotor2Velocity(25);
        compareRecord.setMotor1Sign(Constants.MOTOR_POSITIVE);
        compareRecord.setMotor2Sign(Constants.MOTOR_POSITIVE);
        compareRecord.setNominalAccel(100);
        compareRecord.setMotorVelocityRatio(.5);
        Assertions.assertEquals(testRecord,compareRecord);
    }

    @Test
    public void setCounterClockwiseRatioGreaterThanNegative1NegativeTest(){
        testRecord.setDirection(Constants.CCW_STRING);
        testRecord.setRotationVelocity(50);
        testRecord.setRotationAcceleration(100);
        testRecord.setMotorVelocityRatio(-.5);
        testRecord = testClass.run(testRecord);
        compareRecord.setDirection(Constants.CCW_STRING);
        compareRecord.setRotationVelocity(50);
        compareRecord.setRotationAcceleration(100);
        compareRecord.setMotor1Velocity(50);
        compareRecord.setMotor2Velocity(25);
        compareRecord.setMotor1Sign(Constants.MOTOR_POSITIVE);
        compareRecord.setMotor2Sign(Constants.MOTOR_NEGATIVE);
        compareRecord.setNominalAccel(100);
        compareRecord.setMotorVelocityRatio(-.5);
        Assertions.assertEquals(testRecord,compareRecord);
    }

    @Test
    public void setClockwiseRatioGreaterThan1Test(){
        testRecord.setDirection(Constants.CW_STRING);
        testRecord.setRotationVelocity(50);
        testRecord.setRotationAcceleration(100);
        testRecord.setMotorVelocityRatio(2);
        testRecord = testClass.run(testRecord);
        compareRecord.setDirection(Constants.CW_STRING);
        compareRecord.setRotationVelocity(50);
        compareRecord.setRotationAcceleration(100);
        compareRecord.setMotor1Velocity(50);
        compareRecord.setMotor2Velocity(25);
        compareRecord.setMotor1Sign(Constants.MOTOR_POSITIVE);
        compareRecord.setMotor2Sign(Constants.MOTOR_POSITIVE);
        compareRecord.setNominalAccel(100);
        compareRecord.setMotorVelocityRatio(2);
        Assertions.assertEquals(testRecord,compareRecord);
    }

    @Test
    public void setClockwiseRatioGreaterThan1BackwardsTest(){
        testRecord.setDirection(Constants.CW_STRING);
        testRecord.setRotationVelocity(50);
        testRecord.setRotationAcceleration(100);
        testRecord.setMotorVelocityRatio(2);
        testRecord.setBothMotorsNegativeForRotation(true);
        testRecord = testClass.run(testRecord);
        compareRecord.setDirection(Constants.CW_STRING);
        compareRecord.setRotationVelocity(50);
        compareRecord.setRotationAcceleration(100);
        compareRecord.setMotor1Velocity(50);
        compareRecord.setMotor2Velocity(25);
        compareRecord.setMotor1Sign(Constants.MOTOR_NEGATIVE);
        compareRecord.setMotor2Sign(Constants.MOTOR_NEGATIVE);
        compareRecord.setNominalAccel(100);
        compareRecord.setMotorVelocityRatio(2);
        Assertions.assertEquals(testRecord,compareRecord);

    }

    @Test
    public void setClockwiseRatioLessThanNegative1Test(){
        testRecord.setDirection(Constants.CW_STRING);
        testRecord.setRotationVelocity(50);
        testRecord.setRotationAcceleration(100);
        testRecord.setMotorVelocityRatio(-2);
        testRecord = testClass.run(testRecord);
        compareRecord.setDirection(Constants.CW_STRING);
        compareRecord.setRotationVelocity(50);
        compareRecord.setRotationAcceleration(100);
        compareRecord.setMotor1Velocity(50);
        compareRecord.setMotor2Velocity(25);
        compareRecord.setMotor1Sign(Constants.MOTOR_NEGATIVE);
        compareRecord.setMotor2Sign(Constants.MOTOR_POSITIVE);
        compareRecord.setNominalAccel(100);
        compareRecord.setMotorVelocityRatio(-2);
        Assertions.assertEquals(testRecord,compareRecord);
    }

    @Test
    public void setClockwiseRatioLessThan1PositiveTest(){
        testRecord.setDirection(Constants.CW_STRING);
        testRecord.setRotationVelocity(50);
        testRecord.setRotationAcceleration(100);
        testRecord.setMotorVelocityRatio(.5);
        testRecord = testClass.run(testRecord);
        compareRecord.setDirection(Constants.CW_STRING);
        compareRecord.setRotationVelocity(50);
        compareRecord.setRotationAcceleration(100);
        compareRecord.setMotor1Velocity(25);
        compareRecord.setMotor2Velocity(50);
        compareRecord.setMotor1Sign(Constants.MOTOR_POSITIVE);
        compareRecord.setMotor2Sign(Constants.MOTOR_POSITIVE);
        compareRecord.setNominalAccel(100);
        compareRecord.setMotorVelocityRatio(.5);
        Assertions.assertEquals(testRecord,compareRecord);
    }

    @Test
    public void setClockwiseRatioGreaterThanNegative1NegativeTest(){
        testRecord.setDirection(Constants.CW_STRING);
        testRecord.setRotationVelocity(50);
        testRecord.setRotationAcceleration(100);
        testRecord.setMotorVelocityRatio(-.5);
        testRecord = testClass.run(testRecord);
        compareRecord.setDirection(Constants.CW_STRING);
        compareRecord.setRotationVelocity(50);
        compareRecord.setRotationAcceleration(100);
        compareRecord.setMotor1Velocity(25);
        compareRecord.setMotor2Velocity(50);
        compareRecord.setMotor1Sign(Constants.MOTOR_NEGATIVE);
        compareRecord.setMotor2Sign(Constants.MOTOR_POSITIVE);
        compareRecord.setNominalAccel(100);
        compareRecord.setMotorVelocityRatio(-.5);
        Assertions.assertEquals(testRecord,compareRecord);
    }

    @Test
    public void goForwardToMotor1Test(){
        testRecord.setDirection(Constants.FORWARDS_STRING);
        testRecord.setNominalVelocity(100);
        testRecord.setMotorVelocityRatio(1.1);
        testRecord.setNominalAccel(100);
        testRecord = testClass.run(testRecord);
        compareRecord.setDirection(Constants.FORWARDS_STRING);
        compareRecord.setNominalVelocity(100);
        compareRecord.setMotor1Velocity(100/1.1);
        compareRecord.setMotor2Velocity(100);
        compareRecord.setMotorVelocityRatio(1.1);
        compareRecord.setNominalAccel(100);
        compareRecord.setMotor1Sign(Constants.MOTOR_POSITIVE);
        compareRecord.setMotor2Sign(Constants.MOTOR_POSITIVE);
        Assertions.assertEquals(testRecord,compareRecord);
    }

    @Test
    public void goForwardToMotor2Test(){
        testRecord.setDirection(Constants.FORWARDS_STRING);
        testRecord.setNominalVelocity(80);
        testRecord.setMotorVelocityRatio(.9);
        testRecord.setNominalAccel(80);
        testRecord = testClass.run(testRecord);
        compareRecord.setDirection(Constants.FORWARDS_STRING);
        compareRecord.setNominalVelocity(80);
        compareRecord.setMotor1Velocity(80);
        compareRecord.setMotor2Velocity(80*.9);
        compareRecord.setMotorVelocityRatio(.9);
        compareRecord.setNominalAccel(80);
        compareRecord.setMotor1Sign(Constants.MOTOR_POSITIVE);
        compareRecord.setMotor2Sign(Constants.MOTOR_POSITIVE);
        Assertions.assertEquals(testRecord,compareRecord);
    }

    @Test
    public void goForwardStraightTest(){
        testRecord.setDirection(Constants.FORWARDS_STRING);
        testRecord.setNominalVelocity(40);
        testRecord.setMotorVelocityRatio(1);
        testRecord.setNominalAccel(50);
        testRecord = testClass.run(testRecord);
        compareRecord.setDirection(Constants.FORWARDS_STRING);
        compareRecord.setMotorVelocityRatio(1);
        compareRecord.setNominalVelocity(40);
        compareRecord.setMotor1Velocity(40);
        compareRecord.setMotor2Velocity(40);
        compareRecord.setNominalAccel(50);
        compareRecord.setMotor1Sign(Constants.MOTOR_POSITIVE);
        compareRecord.setMotor2Sign(Constants.MOTOR_POSITIVE);
        Assertions.assertEquals(testRecord,compareRecord);
    }

    @Test
    public void goBackwardsToMotor1Test(){
        testRecord.setDirection(Constants.BACKWARDS_STRING);
        testRecord.setNominalVelocity(100);
        testRecord.setMotorVelocityRatio(1.1);
        testRecord.setNominalAccel(100);
        testRecord = testClass.run(testRecord);
        compareRecord.setDirection(Constants.BACKWARDS_STRING);
        compareRecord.setNominalVelocity(100);
        compareRecord.setMotor1Velocity(100/1.1);
        compareRecord.setMotor2Velocity(100);
        compareRecord.setMotorVelocityRatio(1.1);
        compareRecord.setNominalAccel(100);
        compareRecord.setMotor1Sign(Constants.MOTOR_NEGATIVE);
        compareRecord.setMotor2Sign(Constants.MOTOR_NEGATIVE);
        Assertions.assertEquals(testRecord,compareRecord);
    }

    @Test
    public void goBackwardsToMotor2Test(){
        testRecord.setDirection(Constants.BACKWARDS_STRING);
        testRecord.setNominalVelocity(80);
        testRecord.setMotorVelocityRatio(.9);
        testRecord.setNominalAccel(80);
        testRecord = testClass.run(testRecord);
        compareRecord.setDirection(Constants.BACKWARDS_STRING);
        compareRecord.setNominalVelocity(80);
        compareRecord.setMotor1Velocity(80);
        compareRecord.setMotor2Velocity(80*.9);
        compareRecord.setMotorVelocityRatio(.9);
        compareRecord.setNominalAccel(80);
        compareRecord.setMotor1Sign(Constants.MOTOR_NEGATIVE);
        compareRecord.setMotor2Sign(Constants.MOTOR_NEGATIVE);
        Assertions.assertEquals(testRecord,compareRecord);
    }

    @Test
    public void goBackwardsStraightTest(){
        testRecord.setDirection(Constants.BACKWARDS_STRING);
        testRecord.setNominalVelocity(40);
        testRecord.setMotorVelocityRatio(1);
        testRecord.setNominalAccel(50);
        testRecord = testClass.run(testRecord);
        compareRecord.setDirection(Constants.BACKWARDS_STRING);
        compareRecord.setMotorVelocityRatio(1);
        compareRecord.setNominalVelocity(40);
        compareRecord.setMotor1Velocity(40);
        compareRecord.setMotor2Velocity(40);
        compareRecord.setNominalAccel(50);
        compareRecord.setMotor1Sign(Constants.MOTOR_NEGATIVE);
        compareRecord.setMotor2Sign(Constants.MOTOR_NEGATIVE);
        Assertions.assertEquals(testRecord,compareRecord);
    }
}