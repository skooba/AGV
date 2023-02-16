package com.mochilafulfillment.server.motor_controller;

import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.communications.serial_port.IncorrectResponseFromSerialPortException;
import com.mochilafulfillment.server.communications.serial_port.PortHandler;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.Port;
import java.util.Arrays;

public class MotorOutputCommand {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private PortHandler portHandler;
    private int motorControllerErrorCounter;


    public MotorOutputCommand(PortHandler portHandler) {
        this.portHandler = portHandler;
    }

    public int[] run(int[] command, int messageType) throws SerialPortException, SerialPortTimeoutException, InterruptedException {

        if (portHandler.writeSerialPortBytes(command)) {
            if(messageType == 3){
                try {
                    int[] initialMotorControllerByte = portHandler.readSerialPortBytes(1, false, Constants.MOTOR_CONTROLLER_TIMEOUT);
                    return initialMotorControllerByte;
                } catch (SerialPortTimeoutException e) {
                    logger.warn("Received serial port timeout exception");
                    return new int[1];
                }
            }
            int[] verificationByte;
            try {
                verificationByte = portHandler.readSerialPortBytes(1, false, Constants.MOTOR_CONTROLLER_TIMEOUT);
            } catch (SerialPortTimeoutException e) {
                throw new IncorrectResponseFromSerialPortException(portHandler.getSerialPort().getPortName(), "run", "Expected response of 4F was not received from the controller but was null");
            }
            logger.debug("Verification byte is: " + verificationByte[0]);
            if (verificationByte[0] == MotorControllerConstants.EXPECTED_RESPONSE_FROM_COMMAND) {
                logger.debug("Controller response verified");
                motorControllerErrorCounter = 0;
                if (messageType == 1) {
                    return verificationByte; //will return entire response include 4F verification byte
                } else if (messageType == 2) {
                    return getFilteredResponse();
                } else {
                    throw new IllegalArgumentException("messageType must be either type 1 or 2");
                }
            }
            else {
                logger.warn("Did not receive expected response of 0x4F from controller");
                if(motorControllerErrorCounter > Constants.MOTOR_CONTROLLER_ERROR_COUNTS){ //todo: new - test
                    logger.error("Maximum communication errors reached from motor controller");
                    throw new IncorrectResponseFromSerialPortException(portHandler.getSerialPort().getPortName(), "run", "Expected response of 4F was not received from the controller but was " + verificationByte[0]);
                } else {
                    motorControllerErrorCounter++;
                    logger.warn(motorControllerErrorCounter + " communication errors in a row from motor controller");
                    run(command, messageType);
                }
            }
        } else {
            throw new SerialPortException(portHandler.getSerialPort().getPortName(), "run", "Unsuccessful write to serial port " + portHandler.getSerialPort().getPortName());
        }
        logger.error("Error: Problem with recursive call in program - program never should have gotten here");
        throw new SerialPortException(portHandler.getSerialPort().getPortName(), "run", "Unsuccessful write to serial port " + portHandler.getSerialPort().getPortName());
    }

    public int[] getFilteredResponse() throws SerialPortTimeoutException, SerialPortException {
        int[] firstByteResponse = portHandler.readSerialPortBytes(1,false, Constants.MOTOR_CONTROLLER_TIMEOUT);
        int responseLength = firstByteResponse[0] + 1;
        logger.debug("Length of response array including checksum = " + responseLength);
        int[] restOfTheBytes = portHandler.readSerialPortBytes(responseLength,true, Constants.MOTOR_CONTROLLER_TIMEOUT);
        int[] filteredResponse = ArrayUtils.addAll(firstByteResponse, restOfTheBytes);
        logger.debug("The controller response received is " + Arrays.toString(filteredResponse));
        return filteredResponse;
    }

    public PortHandler getPortHandler() {
        return portHandler;
    }

    public void setPortHandler(PortHandler portHandler){
        this.portHandler = portHandler;
    }
}
