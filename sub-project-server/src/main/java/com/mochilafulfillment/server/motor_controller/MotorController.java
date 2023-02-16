package com.mochilafulfillment.server.motor_controller;

import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.motor_controller.IO.MotorControllerDigitalInputs;
import com.mochilafulfillment.server.motor_controller.IO.MotorControllerDigitalOutputs;
import com.mochilafulfillment.server.motor_controller.IO.MotorControllerMotorOutputs;
import com.mochilafulfillment.server.motor_controller.dtos.MotorControllerRecord;
import com.mochilafulfillment.server.communications.serial_port.PortHandler;
import jssc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;

public class MotorController implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //to handle the case where an exception occurs when trying to shut down motors
    private int stopMotorCounter;

    // Internal MotorController logic
    private PortHandler portHandler;

    private MotorControllerRecord oldControllerRecord;
    private MotorControllerRecord controllerRecord;
    private MotorControllerDigitalInputs digitalInputs;
    private MotorControllerDigitalOutputs digitalOutputs;
    private MotorControllerMotorOutputs motorOutputs;
    private Lock motorControllerLock;

//    private long lastTime = -1;
//    private long currentTime = -1;

    public MotorController(String serialPortName, MotorControllerRecord controllerRecord, Lock motorControllerLock) { // upon MotorController object being created
        logger.debug("Motor Controller port name is " + serialPortName);
        this.portHandler = new PortHandler(new SerialPort(serialPortName));
        this.controllerRecord = controllerRecord;
        digitalInputs = new MotorControllerDigitalInputs(portHandler);
        digitalOutputs = new MotorControllerDigitalOutputs(portHandler);
        motorOutputs = new MotorControllerMotorOutputs(portHandler);
        this.motorControllerLock = motorControllerLock;
    }

    @Override
    public void run () {
        try {
//            lastTime = System.nanoTime();

            portHandler.run(MotorControllerConstants.BAUD_RATE, MotorControllerConstants.PARITY); //A single serial port for both motor controllers
            motorControllerLock.lock();
            try {
                if(motorOutputs.initialize() == true){
                    logger.info("Successfully initialized motors at " + portHandler.getBaudRate() + " baud");
                }
            } finally {
                motorControllerLock.unlock();
            }

            oldControllerRecord = new MotorControllerRecord();
            while (true) { // continues until thread-pool is shutdown in RunProgram

//                currentTime = System.nanoTime();
//                System.out.println("MC loop time is " + ((currentTime - lastTime)/1e9));
//                lastTime = currentTime;

                motorControllerLock.lock();
                try {
                    controllerRecord = digitalInputs.check(controllerRecord); //update record based on input status
                } finally {
                    motorControllerLock.unlock();
                }


                boolean message = true;
                motorControllerLock.lock();
                try {
                    while (controllerRecord.isWarningZone() == true) {
                        if (message == true) {
                            logger.warn("Object in warning zone. Please clear object to continue operation");
                        }
                        message = false;
                        stopAllMovement(controllerRecord, oldControllerRecord);
                        controllerRecord = digitalInputs.check(controllerRecord);
                        oldControllerRecord = new MotorControllerRecord(controllerRecord); //Uses copy constructor
                        Thread.sleep(Constants.LOOP_PAUSE_TIME);
                    }
                } finally {
                    motorControllerLock.unlock();
                }

                motorControllerLock.lock();
                try {
                    if (!oldControllerRecord.equals(controllerRecord)) {
                        logger.debug("Changes in inputs have been made");
                        digitalOutputs.write(controllerRecord, oldControllerRecord);
                        controllerRecord.setNewMotorControllerRecord(true);
                    }
                if (controllerRecord.isNewAgvToMotorControllerRecord()) {
                    logger.debug("New motor outputs to be written");
                    controllerRecord.setNewAgvToMotorControllerRecord(false);
                    motorOutputs.write(controllerRecord, oldControllerRecord);
                }
                oldControllerRecord = new MotorControllerRecord(controllerRecord); //Uses copy constructor
                } finally {
                    motorControllerLock.unlock();
                }

                Thread.sleep(Constants.FAST_LOOP_PAUSE_TIME);
            }
            } catch (Exception e) {
            if (e instanceof InterruptedException) {
                logger.error("Error: Interrupted exception caught");
                throw new RuntimeException("wrapped InterruptedException", e);
            } else if (e instanceof SerialPortTimeoutException) {
                logger.error("Error: Serial port timeout exception caught");
                throw new RuntimeException("wrapped SerialPortTimeoutException", e);
            } else if (e instanceof SerialPortException) {
                logger.error("Error: Serial port exception caught");
                throw new RuntimeException("wrapped SerialPortException", e);
            } else {
                throw new RuntimeException("Did not expect this type of exception", e);
            }
        } finally {
            motorControllerLock.lock();
            stopAllMovement(controllerRecord, oldControllerRecord);
        }
    }

    public void stopAllMovement(MotorControllerRecord controllerRecord, MotorControllerRecord oldControllerRecord) { // not thread safe (needs to be handled in calling method)
        logger.debug("Attempting to stop all motor movements");
        controllerRecord.setStopped();
        try {
            motorOutputs.write(controllerRecord, oldControllerRecord);
            stopMotorCounter = 0;
            logger.debug("Motor movement stopped");
        } catch (SerialPortException | SerialPortTimeoutException | InterruptedException e) {
            stopMotorCounter++;
            if (stopMotorCounter < MotorControllerConstants.NUMBER_OF_STOP_MOTOR_TRIES) {
                stopAllMovement(controllerRecord, oldControllerRecord);
                logger.warn("Stopping motor movement failed on try " + stopMotorCounter);
            } else {
                logger.error("Failure to stop all motor movements");
                throw new RuntimeException("Unable to stop all movement", e);
            }
        }
    }

    public PortHandler getPortHandler() {
        return this.portHandler;
    }

    public void setPortHandler(PortHandler portHandler) {
        this.portHandler = portHandler;
        digitalInputs = new MotorControllerDigitalInputs(portHandler);
        digitalOutputs = new MotorControllerDigitalOutputs(portHandler);
        motorOutputs = new MotorControllerMotorOutputs(portHandler);
    }

    public void setDigitalInputs(MotorControllerDigitalInputs digitalInputs){
        this.digitalInputs = digitalInputs;
    }

    public MotorControllerDigitalInputs getDigitalInputs(){
        return this.digitalInputs;
    }

    public void setDigitalOutputs(MotorControllerDigitalOutputs digitalOutputs){
        this.digitalOutputs = digitalOutputs;
    }

    public void setMotorOutputs(MotorControllerMotorOutputs motorOutputs){
        this.motorOutputs = motorOutputs;
    }

    public void setMotorControllerRecord(MotorControllerRecord controllerRecord){
        this.controllerRecord = controllerRecord;
    }

    public MotorControllerRecord getControllerRecord(){
        return this.controllerRecord;
    }

    public MotorControllerRecord getOldControllerRecord(){
        return this.oldControllerRecord;
    }


    public int getStopMotorCounter(){
        return this.stopMotorCounter;
    }
}