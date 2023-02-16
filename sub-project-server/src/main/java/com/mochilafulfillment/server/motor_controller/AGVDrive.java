package com.mochilafulfillment.server.motor_controller;

import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.dtos.AgvOutputsRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AGVDrive {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private double motorVelocityRatio;
    private int rotationSpeed;
    private int rotationAccel;
    private double nominalSpeed;
    private double nominalAccel;
    private AgvOutputsRecord outputRecord;


    private static final String negative = Constants.MOTOR_NEGATIVE;
    private static final String positive = Constants.MOTOR_POSITIVE;


    public AgvOutputsRecord run(AgvOutputsRecord outputRecord) {
        this.outputRecord = outputRecord;
        motorVelocityRatio = outputRecord.getMotorVelocityRatio();
        rotationSpeed = outputRecord.getRotationVelocity();
        rotationAccel = outputRecord.getRotationAcceleration();
        nominalSpeed = outputRecord.getNominalVelocity();
        nominalAccel = outputRecord.getNominalAccel();
        String direction = outputRecord.getDirection();
        synchronized (AgvOutputsRecord.class) { //no other class can modify AgvOutputRecord instance until it is updated and commands are sent to controller
            switch (direction) {
                case Constants.CW_STRING:
                    setClockwise();
                    break;
                case Constants.CCW_STRING:
                    setCounterClockwise();
                    break;
                case Constants.FORWARDS_STRING:
                    goForward(nominalSpeed);
                    break;
                case Constants.BACKWARDS_STRING:
                    goBackward(nominalSpeed);
                    break;
                default: // handles Stop String case as well
                    setStopped();
                    break;
            }
        }
        return outputRecord;
    }

    private void setStopped() {
        outputRecord.setMotor1Velocity(0);
        outputRecord.setMotor2Velocity(0);
        outputRecord.setNominalAccel(Constants.STOPPING_ACCELERATION);
    }

    private void setClockwise() {
        outputRecord.setNominalAccel(rotationAccel);
        if(motorVelocityRatio != 1){
            if(Math.abs(motorVelocityRatio) > 1){
                outputRecord.setMotor1Velocity(rotationSpeed);
                outputRecord.setMotor2Velocity(rotationSpeed / Math.abs(motorVelocityRatio));
            } else {
                outputRecord.setMotor1Velocity(Math.abs(motorVelocityRatio) * rotationSpeed);
                outputRecord.setMotor2Velocity(rotationSpeed);
            }
            if(motorVelocityRatio > 0 ){
                if(outputRecord.isBothMotorsNegativeForRotation() == false) {
                    outputRecord.setMotor2Sign(positive);
                    outputRecord.setMotor1Sign(positive);
                } else {
                    outputRecord.setMotor2Sign(negative);
                    outputRecord.setMotor1Sign(negative);
                }
            } else {
                outputRecord.setMotor1Sign(negative);
                outputRecord.setMotor2Sign(positive);
            }
        } else {
            outputRecord.setMotor1Velocity(rotationSpeed);
            outputRecord.setMotor2Velocity(rotationSpeed);
            outputRecord.setMotor1Sign(negative);
            outputRecord.setMotor2Sign(positive);
        }
    }

    private void setCounterClockwise() {
        outputRecord.setNominalAccel(rotationAccel);
        if(motorVelocityRatio != 1){
            if(Math.abs(motorVelocityRatio) > 1){
                outputRecord.setMotor2Velocity(rotationSpeed);
                outputRecord.setMotor1Velocity(rotationSpeed / Math.abs(motorVelocityRatio));
            } else {
                outputRecord.setMotor2Velocity(Math.abs(motorVelocityRatio) * rotationSpeed);
                outputRecord.setMotor1Velocity(rotationSpeed);
            }
            if(motorVelocityRatio > 0 ){
                if(outputRecord.isBothMotorsNegativeForRotation() == false) {
                    outputRecord.setMotor2Sign(positive);
                    outputRecord.setMotor1Sign(positive);
                } else {
                    outputRecord.setMotor2Sign(negative);
                    outputRecord.setMotor1Sign(negative);
                }
            } else {
                outputRecord.setMotor2Sign(negative);
                outputRecord.setMotor1Sign(positive);
            }
        } else {
            outputRecord.setMotor1Velocity(rotationSpeed);
            outputRecord.setMotor2Velocity(rotationSpeed);
            outputRecord.setMotor2Sign(negative);
            outputRecord.setMotor1Sign(positive);
        }
    }

    private void goForward(double speed) {
        if (motorVelocityRatio >= 1) { // Reduce speed of motor 1 and AGV turns towards motor 1
            outputRecord.setMotor1Velocity(speed  / motorVelocityRatio);
            outputRecord.setMotor2Velocity(speed);
        } else { // Reduce speed of motor 2 and AGV turns towards motor 2
            outputRecord.setMotor1Velocity(speed);
            outputRecord.setMotor2Velocity(motorVelocityRatio * speed);
        }

        outputRecord.setMotor1Sign(positive);
        outputRecord.setMotor2Sign(positive);
    }

    private void goBackward(double speed) { // motors should operate opposite to go backwards
        //todo: new
        if (motorVelocityRatio >= 1) { // Reduce speed of motor 1 and AGV turns towards motor 1
            outputRecord.setMotor1Velocity(speed  / motorVelocityRatio);
            outputRecord.setMotor2Velocity(speed);
        } else { // Reduce speed of motor 2 and AGV turns towards motor 2
            outputRecord.setMotor1Velocity(speed);
            outputRecord.setMotor2Velocity(motorVelocityRatio * speed);
        }

        outputRecord.setMotor1Sign(negative);
        outputRecord.setMotor2Sign(negative);
    }

}