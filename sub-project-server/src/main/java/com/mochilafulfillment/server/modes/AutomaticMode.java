package com.mochilafulfillment.server.modes;

import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.agv_utils.Exceptions.PGVException;
import com.mochilafulfillment.server.api.PickPath;
import com.mochilafulfillment.server.api.laptop.TerminalReader;
import com.mochilafulfillment.server.controls_logic.Rotation;
import com.mochilafulfillment.server.controls_logic.StopOnTag;
import com.mochilafulfillment.server.controls_logic.StraightLine;
import com.mochilafulfillment.server.controls_logic.ForkLift;
import com.mochilafulfillment.server.controls_logic.dtos.PathRecord;
import com.mochilafulfillment.server.controls_logic.utils.ControlLoop;
import com.mochilafulfillment.server.controls_logic.utils.Lqr;
import com.mochilafulfillment.server.controls_logic.utils.Pid;
import com.mochilafulfillment.server.dtos.*;
import com.mochilafulfillment.server.motor_controller.dtos.MotorControllerRecord;
import com.mochilafulfillment.server.position_scanner.dtos.PositionScannerResponseRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

// Handles the record handoff between services for picking
// Handles management of deleting/fetching path records
public class AutomaticMode implements Mode {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ControlLoop controlLoopCalculator;
    private StraightLine straightLine;
    private final AgvInputsRecord agvInputsRecord = new AgvInputsRecord();
    private PickPath pickPath;
    private PathRecord pathRecord;
    private PositionScannerResponseRecord positionScannerRecord;
    private StopOnTag stopOnTag = new StopOnTag();
    private Rotation rotation = new Rotation();
    private ForkLift forkLift = new ForkLift();

    public AutomaticMode(String csvFileName, int controlLoopType) throws IOException {
        pickPath = new PickPath(csvFileName);
        if (controlLoopType == Constants.PID_CONTROL){
            controlLoopCalculator = new Pid(Constants.KP, Constants.KI, Constants.KD);
        } else if (controlLoopType == Constants.LQR_CONTROL){
            controlLoopCalculator = new Lqr();
        }
        straightLine = new StraightLine(controlLoopCalculator);
    }

    @Override
    public AgvOutputsRecord run(AgvOutputsRecord outputRecord, PositionScannerResponseRecord positionScannerRecord, MotorControllerRecord controllerRecord, TerminalReader terminalReader) throws InterruptedException, PGVException {
        logger.debug("Entering Automatic Mode");

        this.positionScannerRecord = positionScannerRecord;

        if (terminalReader.isExitAutoMode() == true){
            outputRecord = endMode(outputRecord);
            terminalReader.setExitAutoMode(false);
            return outputRecord; //skip the rest
        }

        // Readings from position scanner are sent to AGV record to be used in logic
        if(positionScannerRecord.isNewPositionScannerRecord()) {
            agvInputsRecord.setX(positionScannerRecord.getXPosition());
            agvInputsRecord.setY(positionScannerRecord.getYPosition());
            agvInputsRecord.setTagAngle(positionScannerRecord.getTagAngle());
            agvInputsRecord.setTapeAngle(positionScannerRecord.getTapeAngle());
            agvInputsRecord.setTagId(positionScannerRecord.getTagId());
            agvInputsRecord.setColumnNumber(positionScannerRecord.getColumns());
            agvInputsRecord.setRowNumber(positionScannerRecord.getRows());
            agvInputsRecord.setOnTape(positionScannerRecord.isOnTape());
            positionScannerRecord.setNewPositionScannerRecord(false);
        }

        // Inputs from motor controller are sent to AGV record so can be used in logic
        if (controllerRecord.isNewMotorControllerRecord()) {
            agvInputsRecord.setTopSensorTriggered(controllerRecord.isTopLiftSensor());
            agvInputsRecord.setMiddleSensorTriggered(controllerRecord.isMiddleLiftSensor());
            agvInputsRecord.setBottomSensorTriggered(controllerRecord.isBottomLiftSensor());
            controllerRecord.setNewMotorControllerRecord(false);
        }

        //sets the locks, loads a new path record if necessary
        outputRecord  = decisionTree(outputRecord);

        //run through the 4 modes and only enter the mode if the lock is allowed
        if (outputRecord.getLock() == Constants.STOP_ON_TAG_KEY) {
            outputRecord = straightLine.run(outputRecord, agvInputsRecord);
            outputRecord = stopOnTag.run(outputRecord, agvInputsRecord, pathRecord);
        } else if(outputRecord.getLock() == Constants.ROTATION_KEY){
            outputRecord = rotation.run(outputRecord, agvInputsRecord, pathRecord);
        } else if(outputRecord.getLock() == Constants.FORK_LIFT_KEY) {
            outputRecord = forkLift.run(outputRecord, agvInputsRecord, pathRecord);
        }
        return outputRecord;
    }

    public AgvOutputsRecord decisionTree(AgvOutputsRecord outputRecord) throws InterruptedException {
        if (!outputRecord.getTagIsFinished()) {
            outputRecord.setSafetyScannerMode(Constants.REGULAR_SCAN);
            outputRecord.setLock(Constants.STOP_ON_TAG_KEY);
            logger.debug("Stop On Tag key set");
            outputRecord.setAgvStopped(false);
        } else if (!outputRecord.getRotationIsFinished()) {
            outputRecord.setSafetyScannerMode(Constants.REGULAR_SCAN);
            controlLoopCalculator.resetTerms(); // resets the integral to term to 0 so it restarts next time straightline is called
            if(outputRecord.isAgvStopped()) {
                outputRecord.setLock(Constants.ROTATION_KEY);
                logger.debug("Rotation key set");
                outputRecord.setAgvStopped(false);
            }
        } else if (!outputRecord.getForkLiftIsFinished()) {
            outputRecord.setSafetyScannerMode(Constants.PICKING_SCAN);
            if(outputRecord.isAgvStopped()) {
                outputRecord.setLock(Constants.FORK_LIFT_KEY);
                logger.debug("Forklift key set");
                outputRecord.setAgvStopped(false);
            }
        } else { // resets and loads new path record
            pathRecord = pickPath.getNext();
            if (pathRecord == null){
                logger.warn("Path record is empty or completed");
                outputRecord.setPathFinished(true);
                return outputRecord;
            }
            outputRecord.setSafetyScannerMode(Constants.REGULAR_SCAN);
            outputRecord.setRotationIsFinished(false);
            outputRecord.setTagIsFinished(false);
            outputRecord.setForkLiftIsFinished(false);
            outputRecord.setLock(0);
            logger.debug("Next tagID = " + pathRecord.getTagId());
            logger.debug("Next rotation = " + pathRecord.getRotateToDegrees());
            if (pathRecord.isPickLine()) {
                logger.debug("Pick line");
            } else if (pathRecord.isLiftLine()) {
                logger.debug("Lift line");
            } else if (pathRecord.isLowerLine())
                logger.debug("Lower line");
            //Start the AGV moving towards its next tag
            if(pathRecord.isLowerLine()){
                logger.debug("AGV will move backwards with pallet/shelf");
                outputRecord.setDirection(Constants.BACKWARDS_STRING);
                outputRecord.setNominalAccel(Constants.BACKWARDS_ACCELERATION);
                outputRecord.setNominalVelocity(Constants.BACKWARDS_VELOCITY);
            } else {
                outputRecord.setDirection(Constants.FORWARDS_STRING);
                outputRecord.setNominalAccel(Constants.STANDARD_ACCELERATION);
                outputRecord.setNominalVelocity(Constants.STANDARD_VELOCITY);
            }
        }
        return outputRecord;
    }

    @Override
    public AgvOutputsRecord endMode(AgvOutputsRecord outputRecord) {
        logger.debug("Exiting auto mode");
        outputRecord.setModeFinished(true);
        return outputRecord;
    }

    public PositionScannerResponseRecord getPositionScannerResponseRecord(){
        return this.positionScannerRecord;
    }

    public AgvInputsRecord getAgvInputsRecord(){
        return this.agvInputsRecord;
    }

    public PathRecord getPathRecord(){
        return this.pathRecord;
    }

    public PickPath getPickPath(){
        return this.pickPath;
    }

    public void setPathRecord(PathRecord pathRecord){
        this.pathRecord = pathRecord;
    }

    public void setPickPath(PickPath pickPath){
        this.pickPath = pickPath;
    }

    public void setStraightLine(StraightLine straightLine) {
        this.straightLine = straightLine;
    }

    public void setStopOnTag(StopOnTag stopOnTag) {
        this.stopOnTag = stopOnTag;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }

    public void setForkLift(ForkLift forkLift) {
        this.forkLift = forkLift;
    }

    public ControlLoop getControlLoopCalculator(){
        return controlLoopCalculator;
    }

}
