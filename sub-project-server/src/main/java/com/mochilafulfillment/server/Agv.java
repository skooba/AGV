package com.mochilafulfillment.server;

import com.mochilafulfillment.server.api.GetOperationMode;
import com.mochilafulfillment.server.api.laptop.TerminalReader;
import com.mochilafulfillment.server.dtos.AgvOutputsRecord;
import com.mochilafulfillment.server.modes.RemoteControlMode;
import com.mochilafulfillment.server.modes.AutomaticMode;
import com.mochilafulfillment.server.modes.Mode;
import com.mochilafulfillment.server.motor_controller.AGVDrive;
import com.mochilafulfillment.server.motor_controller.dtos.MotorControllerRecord;
import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.position_scanner.dtos.PositionScannerResponseRecord;
import com.mochilafulfillment.shared.SharedConstants;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.locks.Lock;

public class Agv implements Runnable{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private TerminalReader terminalReader;
    private PositionScannerResponseRecord positionScannerRecord;
    private MotorControllerRecord controllerRecord;
    private Mode modeObject;
    private AGVDrive agvDrive = new AGVDrive();
    private String fileName = Constants.FILE_NAME;
    private AgvOutputsRecord outputRecord = new AgvOutputsRecord();
    private Lock motorControllerLock;
    private int motor1Counter = -1;
    private int motor2Counter = -1;

    private boolean keepLooping = true;

    public Agv(TerminalReader terminalReader, MotorControllerRecord controllerRecord, PositionScannerResponseRecord positionScannerRecord, Lock motorControllerLock) {
        this.terminalReader = terminalReader;
        this.controllerRecord = controllerRecord;
        this.positionScannerRecord = positionScannerRecord;
        this.motorControllerLock = motorControllerLock;
    }

    // This loop sets the pace for all dynamic agv controls logic
    public void run() {
        try {
            AgvOutputsRecord oldOutputRecord = new AgvOutputsRecord();
            modeObject = initialOperationMode();
            if (modeObject == null) { //Terminate was entered before a mode was chosen
                keepLooping = false;
            }

            while (keepLooping) {

                outputRecord = modeObject.run(outputRecord, positionScannerRecord, controllerRecord, terminalReader); //update last output record from previous loop based on AGV state

                if (outputRecord.isPathFinished() || outputRecord.isModeFinished()) {
                    motorControllerLock.lock();
                    try {
                        controllerRecord.setStopped();
                    } finally {
                        motorControllerLock.unlock();
                    }
                    modeObject = initialOperationMode();
                    outputRecord = new AgvOutputsRecord();
                    outputRecord = modeObject.run(outputRecord, positionScannerRecord, controllerRecord, terminalReader); //update last output record from previous loop based on AGV state
                }

                if (terminalReader.isEndProgram()) {
                    logger.debug("Program termination key was entered");
                    break;
                }

                //Sets output records parameters based on directions set in the 4 modes
                outputRecord = agvDrive.run(outputRecord);

                //Logic outputs are sent to controller record to be sent directly to controller serially
                if (!oldOutputRecord.equalsMotorController(outputRecord) || motor1Counter != -1 || motor2Counter != -1) {
                    logger.debug("records don't match, sending new AGVOutputRecords to motor controller");
                    motorControllerLock.lock();
                    try {
                        //Ensure that the AGV doesn't speed up or slow down too fast
                        if(Math.abs(outputRecord.getMotor1Velocity() - controllerRecord.getMotor1Velocity()) > Constants.MAX_VELOCITY_CHANGE){
                            if(motor1Counter >= Constants.NUMBER_OF_LOOPS_BEFORE_CHANGE_VELOCITY || motor1Counter == -1){
                                if(outputRecord.getMotor1Velocity() > controllerRecord.getMotor1Velocity()) {
                                    logger.debug("incrementing motor1 speed up");
                                    controllerRecord.setMotor1Velocity(controllerRecord.getMotor1Velocity() + Constants.MAX_VELOCITY_CHANGE);
                                } else {
                                    logger.debug("incrementing motor1 speed down");
                                    controllerRecord.setMotor1Velocity(controllerRecord.getMotor1Velocity() - Constants.MAX_VELOCITY_CHANGE);
                                }
                                motor1Counter = 0;
                            } else {
                                motor1Counter++;
                            }
                        } else {
                            logger.debug("setting motor1 speed to final command value");
                            controllerRecord.setMotor1Velocity(outputRecord.getMotor1Velocity());

                            motor1Counter = -1;
                        }


                        if(Math.abs(outputRecord.getMotor2Velocity() - controllerRecord.getMotor2Velocity()) > Constants.MAX_VELOCITY_CHANGE){
                            if(motor2Counter >= Constants.NUMBER_OF_LOOPS_BEFORE_CHANGE_VELOCITY || motor2Counter == -1){
                                if(outputRecord.getMotor2Velocity() > controllerRecord.getMotor2Velocity()) {
                                    logger.debug("incrementing motor2 speed up");
                                    controllerRecord.setMotor2Velocity(controllerRecord.getMotor2Velocity() + Constants.MAX_VELOCITY_CHANGE);
                                } else {
                                    logger.debug("incrementing motor2 speed down");
                                    controllerRecord.setMotor2Velocity(controllerRecord.getMotor2Velocity() - Constants.MAX_VELOCITY_CHANGE);
                                }
                                motor2Counter = 0;
                            } else {
                                motor2Counter++;
                            }
                        } else {
                            logger.debug("setting motor2 speed to final command value");

                            controllerRecord.setMotor2Velocity(outputRecord.getMotor2Velocity());

                            motor2Counter = -1;
                        }

                        if(controllerRecord.getMotor1Velocity() == 0 && controllerRecord.getMotor2Velocity() == 0){
                            outputRecord.setAgvStopped(true);
                            logger.debug("AGV is fully stopped");
                        }

                        controllerRecord.setNominalAccel(outputRecord.getNominalAccel());
                        controllerRecord.setDirection(outputRecord.getDirection());
                        controllerRecord.setMotor1Sign(outputRecord.getMotor1Sign());
                        controllerRecord.setMotor2Sign(outputRecord.getMotor2Sign());
                        controllerRecord.setLiftType(outputRecord.getLiftType());
                        controllerRecord.setSafetyScannerMode(outputRecord.getSafetyScannerMode());
                        controllerRecord.setNewAgvToMotorControllerRecord(true);
                    } finally {
                        if(controllerRecord.isNewAgvToMotorControllerRecord() == true) {
                            motorControllerLock.unlock();
                            if (outputRecord.getDirection() == Constants.STOP_STRING && controllerRecord.getMotor1Velocity() == 0 && controllerRecord.getMotor2Velocity() == 0) { //todo: velocities being 0 is new
                                logger.debug("Delaying because Stop String was set and motors are at 0 velocity");
                                Thread.sleep(Constants.STOP_PAUSE_TIME);
                            }
                        }
                    }
                    oldOutputRecord = new AgvOutputsRecord(outputRecord);
                } else {
                    logger.debug("records match, do not modify motorController record");
                }
                Thread.sleep(Constants.LOOP_PAUSE_TIME); //Give some time for other threads to process since AGV logic runs very fast and will jam up serial communication
            }

        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                throw new RuntimeException("wrapped InterruptedException", e);
            } else if (e instanceof SerialPortTimeoutException) {
                throw new RuntimeException("wrapped SerialPortTimeoutException", e);
            } else if (e instanceof SerialPortException) {
                throw new RuntimeException("wrapped SerialPortException", e);
            }
        } finally {
            //When loop is terminated, tell the controllerRecord to stop motors and stop lift
            motorControllerLock.lock();
            try {
                controllerRecord.setStopped();
            } finally {
                motorControllerLock.unlock();
            }
            logger.debug("Stopped motors, terminated AGV loop");
            try {
                Thread.sleep(Constants.PAUSE_BEFORE_END); //pause before program ends so motor controller can stop based on updated record
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public RemoteControlMode changeModeToRemote() throws IOException {
        RemoteControlMode remoteMode = new RemoteControlMode(SharedConstants.TCP_PORT);
        return remoteMode;
    }

    public AutomaticMode changeModeToAuto() throws IOException {
        AutomaticMode autoMode = new AutomaticMode(fileName, Constants.CONTROL_LOOP_TYPE);
        return autoMode;
    }


    public Mode initialOperationMode() throws InterruptedException, IOException {
        Mode initialModeObject = null;
        GetOperationMode getOperationMode = new GetOperationMode();
        String modeType = getOperationMode.run(terminalReader);
        if (modeType == Constants.REMOTE_CONTROL_MODE) {
            initialModeObject = changeModeToRemote();
        } else if (modeType == Constants.AUTOMATIC_MODE) {
            initialModeObject = changeModeToAuto();
        }
        logger.debug("Initial mode has been chosen");
        terminalReader.setExitAutoMode(false);
        return initialModeObject;
    }

    public void setTerminalReader(TerminalReader terminalReader) {
        this.terminalReader = terminalReader;
    }

    public void setAgvOutputs(AgvOutputsRecord outputRecord) {
        this.outputRecord = outputRecord;
    }

    public AgvOutputsRecord getAgvOutputs() {
        return outputRecord;
    }

    public Mode getModeObject(){
        return this.modeObject;
    }

    public MotorControllerRecord getControllerRecord(){
        return controllerRecord;
    }

    public void setControllerRecord(MotorControllerRecord controllerRecord){
        this.controllerRecord = controllerRecord;
    }

    public void setAgvDrive(AGVDrive agvDrive){
        this.agvDrive = agvDrive;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setMotor1Counter(int motor1Counter) {
        this.motor1Counter = motor1Counter;
    }

    public void setMotor2Counter(int motor2Counter) {
        this.motor2Counter = motor2Counter;
    }

    public void setModeObject(Mode modeObject) {
        this.modeObject = modeObject;
    }

}
