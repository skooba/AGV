package com.mochilafulfillment.server.motor_controller.IO;

import com.mochilafulfillment.server.agv_utils.ByteToolbox;
import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.communications.serial_port.IncorrectResponseFromSerialPortException;
import com.mochilafulfillment.server.motor_controller.MotorControllerConstants;
import com.mochilafulfillment.server.motor_controller.MotorOutputCommand;
import com.mochilafulfillment.server.motor_controller.dtos.MotorControllerRecord;
import com.mochilafulfillment.server.communications.serial_port.PortHandler;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MotorControllerMotorOutputs {
//    private long lastTime = -1;
//    private long currentTime = -1;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private MotorOutputCommand motorControllerWrite;
    private PortHandler portHandler;

    private final byte byteLengthMove = 8;


    public MotorControllerMotorOutputs(PortHandler portHandler) {
        this.portHandler = portHandler;
        this.motorControllerWrite = new MotorOutputCommand(portHandler);
    }

    // Begins writing velocities and accelerations to the motor controller
    public void write(MotorControllerRecord controllerOutputRecord, MotorControllerRecord oldControllerOutputRecord) throws SerialPortException, SerialPortTimeoutException, InterruptedException {
        boolean compareAccel = Math.round(controllerOutputRecord.getNominalAccel() * MotorControllerConstants.ACCEL_MULTIPLIER) == Math.round(oldControllerOutputRecord.getNominalAccel() * MotorControllerConstants.ACCEL_MULTIPLIER);


        if (!compareAccel) {
            logger.debug("new acceleration to be written");
            writeAccel(controllerOutputRecord);
        } else {
            logger.debug("No acceleration updates to controller record");
        }

        boolean compareV1Sign = controllerOutputRecord.getMotor1Sign() == oldControllerOutputRecord.getMotor1Sign();
        boolean compareV2Sign = controllerOutputRecord.getMotor2Sign() == oldControllerOutputRecord.getMotor2Sign();
        boolean compareV1 = Math.round(controllerOutputRecord.getMotor1Velocity() * MotorControllerConstants.VELOCITY_MULTIPLIER) == Math.round(oldControllerOutputRecord.getMotor1Velocity() * MotorControllerConstants.VELOCITY_MULTIPLIER);
        boolean compareV2 = Math.round(controllerOutputRecord.getMotor2Velocity() * MotorControllerConstants.VELOCITY_MULTIPLIER) == Math.round(oldControllerOutputRecord.getMotor2Velocity() * MotorControllerConstants.VELOCITY_MULTIPLIER);

        if (!compareV1Sign | !compareV1) {
            logger.debug("new velocity written to controller 1");
            writeVelocity1(controllerOutputRecord);
        } else {
            logger.debug("no velocity written to controller 1");
        }

        if (!compareV2Sign | !compareV2) {
            logger.debug("new velocity written to controller 2");
            writeVelocity2(controllerOutputRecord);
        } else {
            logger.debug("no velocity written to controller 2");
        }

    }


    public void writeVelocity1(MotorControllerRecord controllerOutputRecord) throws SerialPortException, SerialPortTimeoutException, InterruptedException {

        //scales velocities from 0-100% to Internal Units (IU) of the Technosoft controller
        int motor1Velocity = (int)Math.round(controllerOutputRecord.getMotor1Velocity() * MotorControllerConstants.VELOCITY_MULTIPLIER);

//        System.out.println("Writing motor1Velocity : " + motor1Velocity);
//        System.out.println("motor1Velocity sign : " + controllerOutputRecord.getMotor1Sign());


        logger.debug("internal units (IU) velocity of controller axis1  = " + motor1Velocity);

        int[] motor1VelocityBytes = velocityAndAccelerationToByteArray(motor1Velocity,controllerOutputRecord.getMotor1Sign());

        //Set the byte arrays up with a zero in the checksum
        int[] motor1WriteVelocityByteArray = new int[]{byteLengthMove, MotorControllerConstants.AXIS_1_ID_HIGH, MotorControllerConstants.AXIS_1_ID_LOW, MotorControllerConstants.COMMAND_VELOCITY_CODE_HIGH,
                MotorControllerConstants.COMMAND_VELOCITY_CODE_LOW, motor1VelocityBytes[0], motor1VelocityBytes[1], motor1VelocityBytes[2], motor1VelocityBytes[3], 0};


        //Compute the checksum and then finish the byte arrays
        int checkSumAxis1 = IOUtils.checkSum(motor1WriteVelocityByteArray);
        motor1WriteVelocityByteArray[byteLengthMove+1] = checkSumAxis1;

        // No other serial calls allowed until speeds are written
        motorControllerWrite.run(motor1WriteVelocityByteArray,1);
    }

    public void writeVelocity2(MotorControllerRecord controllerOutputRecord) throws SerialPortException, SerialPortTimeoutException, InterruptedException {

        //scales velocities from 0-100% to Internal Units (IU) of the Technosoft controller
        int motor2Velocity = (int)Math.round(controllerOutputRecord.getMotor2Velocity() * MotorControllerConstants.VELOCITY_MULTIPLIER);

//        System.out.println("Writing motor2Velocity : " + motor2Velocity);
//        System.out.println("motor2Velocity sign : " + controllerOutputRecord.getMotor2Sign());


        logger.debug("internal units (IU) velocity of controller axis2  =  " + motor2Velocity);

        int[] motor2VelocityBytes = velocityAndAccelerationToByteArray(motor2Velocity, controllerOutputRecord.getMotor2Sign());

        //Set the byte arrays up with a zero in the checksum

        int[] motor2WriteVelocityByteArray = new int[]{byteLengthMove, MotorControllerConstants.AXIS_2_ID_HIGH, MotorControllerConstants.AXIS_2_ID_LOW, MotorControllerConstants.COMMAND_VELOCITY_CODE_HIGH,
                MotorControllerConstants.COMMAND_VELOCITY_CODE_LOW, motor2VelocityBytes[0], motor2VelocityBytes[1], motor2VelocityBytes[2], motor2VelocityBytes[3], 0};

        //Compute the checksum and then finish the byte arrays
        int checkSumAxis2 = IOUtils.checkSum(motor2WriteVelocityByteArray);
        motor2WriteVelocityByteArray[byteLengthMove+1] = checkSumAxis2;

        // No other serial calls allowed until speeds are written
        motorControllerWrite.run(motor2WriteVelocityByteArray, 1);
    }

    public void writeAccel(MotorControllerRecord controllerOutputRecord) throws SerialPortException, SerialPortTimeoutException, InterruptedException {

        int acceleration = (int) Math.round(controllerOutputRecord.getNominalAccel() * MotorControllerConstants.ACCEL_MULTIPLIER);

//        System.out.println("Writing acceleration : " + acceleration);

        logger.debug("internal units (IU) acceleration of controllers  = " + acceleration);

        int[] motorAccelerationBytes = velocityAndAccelerationToByteArray(acceleration,Constants.MOTOR_POSITIVE);

        int[] motor1WriteAccelerationByteArray = new int[]{byteLengthMove, MotorControllerConstants.AXIS_1_ID_HIGH, MotorControllerConstants.AXIS_1_ID_LOW,
                MotorControllerConstants.COMMAND_ACCELERATION_CODE_HIGH, MotorControllerConstants.COMMAND_ACCELERATION_CODE_LOW, motorAccelerationBytes[0],
                motorAccelerationBytes[1], motorAccelerationBytes[2], motorAccelerationBytes[3], 0};
        int[] motor2WriteAccelerationByteArray = new int[]{byteLengthMove, MotorControllerConstants.AXIS_2_ID_HIGH, MotorControllerConstants.AXIS_2_ID_LOW,
                MotorControllerConstants.COMMAND_ACCELERATION_CODE_HIGH, MotorControllerConstants.COMMAND_ACCELERATION_CODE_LOW, motorAccelerationBytes[0],
                motorAccelerationBytes[1], motorAccelerationBytes[2], motorAccelerationBytes[3], 0};

        //Compute the checksum and then finish the byte arrays
        int checkSumAxis1 = IOUtils.checkSum(motor1WriteAccelerationByteArray);
        int checkSumAxis2 = IOUtils.checkSum(motor2WriteAccelerationByteArray);
        motor1WriteAccelerationByteArray[byteLengthMove+1] = checkSumAxis1;
        motor2WriteAccelerationByteArray[byteLengthMove+1] = checkSumAxis2;

        motorControllerWrite.run(motor1WriteAccelerationByteArray, 1);
        motorControllerWrite.run(motor2WriteAccelerationByteArray, 1);
    }

    //Logic determined by testing different serial commands in Technosoft's EasyMotionStudio software
    public int[] velocityAndAccelerationToByteArray(int velocity, String motorSign) {
        long signedVelocity = 0;
        if(motorSign == Constants.MOTOR_NEGATIVE){
            velocity *= -1;
        }
        if((velocity >65535) || (velocity < -65536)) {
            throw new IllegalArgumentException("Error: Velocity input is out of range");
        }else if((velocity > 32767) || (velocity < -32768)){
            if (motorSign == Constants.MOTOR_POSITIVE) {
                signedVelocity = ByteToolbox.twosComplement(velocity,16) << 16;
            } else if (motorSign == Constants.MOTOR_NEGATIVE){
                long signedVelocityBytes3and4 = ByteToolbox.twosComplement(velocity,16) << 16;
                long mask = (long)Math.pow(2, 16) - 1;
                long secondMask = (long)Math.pow(2, 32) - 1;
                signedVelocity = (signedVelocityBytes3and4 | mask) & secondMask;
            }
        } else {
            signedVelocity = ByteToolbox.twosComplement(velocity,16);
        }
        return  ByteToolbox.intToByteArray(signedVelocity,8);
    }

    public boolean initialize() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        logger.debug("Initializing Motors");
        if (testController() == true) {
            return true;
        } else {
            logger.warn("Initialization string did not initialize motors");
            if (establishCommunication()) {
                logger.debug("Communication established with drive");
                if (testController() == true) {
                    return true;
                } else {
                    logger.warn("Initialization string did not initialize motors");
                }
            } else {
                logger.warn("Initialization failed at " + portHandler.getBaudRate());
                portHandler.changeBaud(MotorControllerConstants.RESTART_BAUD_RATE);
                if (establishCommunication()) {
                    logger.debug("Communication established with drive");
                    if (testController() == true) {
                        motorControllerWrite.run(MotorControllerConstants.SET_CONTROLLER_BAUD_TO_115200, 1);
                        portHandler.changeBaud(MotorControllerConstants.BAUD_RATE);
                        if (establishCommunication()) {
                            logger.debug("Communication established with drive");
                            if (testController() == true) {
                                return true;
                            } else {
                                logger.warn("Initialization string did not initialize motors");
                            }
                        }
                    } else {
                        logger.warn("Initialization string did not initialize motors");
                    }
                } else {
                    logger.error("Error: Initialization failed at " + portHandler.getBaudRate());
                }
            }
        }
        logger.debug("Motor controllers not initialized");
        throw new IncorrectResponseFromSerialPortException(portHandler.getSerialPort().getPortName(), "initialize", "Initialization failed");
    }

    public boolean testController() throws SerialPortException, SerialPortTimeoutException, InterruptedException {
        int[] testCommand = new int[]{0x08, 0x00, 0x00, 0xB0, 0x00, 0x00, 0x01, 0x00, 0x00, 0xB9};

        try {
            motorControllerWrite.run(testCommand, 1);
            portHandler.readSerialPortBytes(12, true, Constants.MOTOR_CONTROLLER_TIMEOUT);
            motorControllerWrite.run(MotorControllerConstants.AXIS_1_ON, 1);
            motorControllerWrite.run(MotorControllerConstants.AXIS_2_ON, 1);
            motorControllerWrite.run(MotorControllerConstants.MODE_SP_AXIS_1, 1);
            motorControllerWrite.run(MotorControllerConstants.MODE_SP_AXIS_2, 1);
            return true;
        } catch (IncorrectResponseFromSerialPortException e){
            logger.error("Error: Did not receive expected initialization byte");
            return false;
        }
    }

    public boolean establishCommunication() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        int[] initializeMotors2 = new int[]{MotorControllerConstants.MOTOR_CONTROLLER_INITIAL_REQUEST};

        for (int i = 0; i < MotorControllerConstants.INITIALIZATION_TRIES; i++) {
            int response = motorControllerWrite.run(initializeMotors2, 3)[0];
            if (response == 0) {
                logger.debug("No response from 0xFF request");
            } else if (response == MotorControllerConstants.MOTOR_CONTROLLER_INITIAL_RESPONSE) {
                logger.debug("Expected response received from 0xFF request");
                return true;
            } else {
                logger.warn("Unexpected response received from 0xFF request: " + response);
            }

        }
        return false;
    }

    public void setMotorControllerWrite(MotorOutputCommand motorControllerWrite){
        this.motorControllerWrite = motorControllerWrite;
    }
}
