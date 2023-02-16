package com.mochilafulfillment.server.controls_logic;

import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.controls_logic.dtos.PathRecord;
import com.mochilafulfillment.server.dtos.AgvInputsRecord;
import com.mochilafulfillment.server.dtos.AgvOutputsRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ForkLift {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AgvOutputsRecord run(AgvOutputsRecord outputRecord, AgvInputsRecord inputRecord, PathRecord pathRecord) throws InterruptedException {

        if (pathRecord.isLiftLine()) {
            outputRecord = lift(outputRecord, inputRecord);
        } else if (pathRecord.isPickLine()) {
            outputRecord = pick(outputRecord, inputRecord);
        } else if (pathRecord.isLowerLine()) {
            outputRecord = lower(outputRecord, inputRecord);
        } else if (pathRecord.isPlaceLine()) {
            outputRecord = place(outputRecord, inputRecord);
        } else {
            logger.debug("Not a forklift step");
            outputRecord.setForkLiftIsFinished(true);
        }
        return outputRecord;
    }

    //A lift raises the forks of AGV to the middle sensor
    public AgvOutputsRecord lift(AgvOutputsRecord outputRecord, AgvInputsRecord inputRecord) throws InterruptedException {
        if (outputRecord.getLiftType() == Constants.STOP_VERTICAL_CONSTANT) { // If lift isn't already moving
            if (inputRecord.isTopSensorTriggered()) {
                outputRecord.setLiftType(Constants.LIFT_CONSTANT);
            } else {
                logger.error("Error: Told to lift but top sensor not triggered");
            }
        } else if (!(outputRecord.getLiftType() == Constants.STOP_VERTICAL_CONSTANT)) { // If lift is already moving
            if (inputRecord.isMiddleSensorTriggered()) {
                outputRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);
                outputRecord.setForkLiftIsFinished(true);
                logger.debug("Lifting complete");
            } else if (inputRecord.isTopSensorTriggered()) {
                outputRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);
                logger.error("Error: Middle sensor did not trigger");
            } else {
                logger.debug("Lift lifting");
            }
        }
        return outputRecord; // return with no changes
    }

    //A place lowers the forks of AGV to the middle sensor
    public AgvOutputsRecord place(AgvOutputsRecord outputRecord, AgvInputsRecord inputRecord) throws InterruptedException { //todo: new - test
        if (outputRecord.getLiftType() == Constants.STOP_VERTICAL_CONSTANT) { // If lift isn't already moving
            if (inputRecord.isBottomSensorTriggered()) {
                outputRecord.setLiftType(Constants.LOWER_CONSTANT);
            } else {
                logger.error("Error: Told to place but bottom sensor not triggered");
            }
        } else if (!(outputRecord.getLiftType() == Constants.STOP_VERTICAL_CONSTANT)) { // If lift is already moving
            if (inputRecord.isMiddleSensorTriggered()) {
                outputRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);
                outputRecord.setForkLiftIsFinished(true);
                logger.debug("Placing complete");
            } else if (inputRecord.isTopSensorTriggered()) {
                outputRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);
                logger.error("Error: Middle sensor did not trigger");
            } else {
                logger.debug("Lift placing");
            }
        }
        return outputRecord; // return with no changes
    }


    //A pick raises the forks of AGV to the top sensor
    public AgvOutputsRecord pick(AgvOutputsRecord outputRecord, AgvInputsRecord inputs) throws InterruptedException {
        if (outputRecord.getLiftType() == Constants.STOP_VERTICAL_CONSTANT) { // If lift isn't already moving
            if (inputs.isMiddleSensorTriggered() || inputs.isTopSensorTriggered()) {
                outputRecord.setLiftType(Constants.LIFT_CONSTANT);
            } else {
                logger.error("Error: Told to pick but neither middle nor top sensors are triggered");
            }
        } else if (!(outputRecord.getLiftType() == Constants.STOP_VERTICAL_CONSTANT)) { // If lift is already moving
            if (inputs.isBottomSensorTriggered()) {
                outputRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);
                outputRecord.setLock(0);
                outputRecord.setForkLiftIsFinished(true);
                logger.debug("Picking complete");
            } else if (inputs.isTopSensorTriggered()) {
                outputRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);
                logger.error("Error: Lift went down instead of up");
            } else {
                logger.debug("Lift picking");
            }
        }
        return outputRecord; // return with no changes
    }

    //A lower raises the forks of AGV to the bottom sensor
    public AgvOutputsRecord lower(AgvOutputsRecord outputRecord, AgvInputsRecord inputs) throws InterruptedException {
        if (outputRecord.getLiftType() == Constants.STOP_VERTICAL_CONSTANT) { // If lift isn't already moving
            if (inputs.isBottomSensorTriggered() || inputs.isMiddleSensorTriggered()) {
                outputRecord.setLiftType(Constants.LOWER_CONSTANT);
            } else {
                logger.error("Error: Told to lower but neither bottom sensor nor middle sensor are not triggered");
            }
        } else if (!(outputRecord.getLiftType() == Constants.STOP_VERTICAL_CONSTANT)) { // If lift is already moving
            if (inputs.isTopSensorTriggered()) {
                outputRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);
                outputRecord.setForkLiftIsFinished(true);
                logger.debug("Lowering complete");
            } else if (inputs.isBottomSensorTriggered()) {
                outputRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);
                logger.error("Error: Lift went up instead of down");
            } else {
                logger.debug("Lift lowering");
            }
        }
        return outputRecord;
    }
}