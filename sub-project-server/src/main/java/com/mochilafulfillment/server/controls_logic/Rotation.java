package com.mochilafulfillment.server.controls_logic;

import com.mochilafulfillment.server.controls_logic.utils.ControlLoop;
import com.mochilafulfillment.server.controls_logic.utils.LqrRotation;
import com.mochilafulfillment.server.dtos.AgvOutputsRecord;
import com.mochilafulfillment.server.dtos.AgvInputsRecord;
import com.mochilafulfillment.server.controls_logic.dtos.RotationRecord;
import com.mochilafulfillment.server.controls_logic.dtos.PathRecord;
import com.mochilafulfillment.server.agv_utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Logic for checking and acting upon tag turn command from PathRecord API
public class Rotation{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private RotationRecord rotationRecord = new RotationRecord();
    ControlLoop controlLoopCalculator = new LqrRotation();

    private static int rotationType = Constants.ROTATION_TYPE; //static for testing purposes

    public AgvOutputsRecord run(AgvOutputsRecord outputRecord, AgvInputsRecord inputRecord, PathRecord pathRecord) throws InterruptedException {

        int rotateToDegrees = pathRecord.getRotateToDegrees();
        double angle = inputRecord.getTagAngle(); //Angle from the scanner
        double rotateByDegrees = (rotateToDegrees - angle) % 360;
        logger.debug("In total the AGV has to rotate by " + rotateByDegrees + " degrees");
        if (rotationRecord.isStartedRotation()) { //If NOT the first loop
            if (Math.abs(rotateByDegrees) <= Constants.FINAL_ANGLE_TOLERANCE) {
                logger.debug("On final rotation angle");
                return runOnFinalAngle(outputRecord, rotationRecord);
            } else {
                rotationRecord.setOnTargetAngleCounter(1);
                logger.debug("AGV continues to rotate");
                return toTarget(outputRecord, angle, rotateToDegrees, inputRecord);
            }
        } else { // If the first loop
            if (Math.abs(rotateByDegrees) > Constants.FINAL_ANGLE_TOLERANCE) {
                logger.debug("The AGV is starting at an angle of " + angle + " degrees");
                rotationRecord.setStartedRotation(true); //Automatically set to "false" when new RotationRecord created
                return toTarget(outputRecord, angle, rotateToDegrees, inputRecord);
            } else {
                logger.debug("no rotation required");
                outputRecord.setRotationIsFinished(true);
                return (outputRecord);
            }
        }
    }

    public AgvOutputsRecord toTarget(AgvOutputsRecord outputRecord, double angle, int rotateToDegrees, AgvInputsRecord inputRecord) {
        double rotationLeft = (rotateToDegrees - angle) % 360;

        if (Math.abs(rotationLeft) > 180){
            rotationLeft = -(rotationLeft - 180);
        }
        logger.debug("AGV has " + rotationLeft + " degrees more to rotate");

        if (rotationLeft < 0){
            logger.debug("Counter-clockwise rotation");
            outputRecord.setDirection(Constants.CCW_STRING);
            if(rotationType == Constants.LQR_ROTATION) {
                double[] iterationResponse = controlLoopCalculator.iteration(inputRecord.getY(), inputRecord.getX(), inputRecord.getTagAngle(), Constants.ROTATE_SPEED);
                outputRecord.setMotorVelocityRatio(iterationResponse[0]);
                if (iterationResponse[1] < 0 && iterationResponse[2] < 0 ){
                    outputRecord.setBothMotorsNegativeForRotation(true);
                } else {
                    outputRecord.setBothMotorsNegativeForRotation(false);
                }
            }
        }
        else{
            logger.debug("Clockwise rotation");
            outputRecord.setDirection(Constants.CW_STRING);
            if(rotationType == Constants.LQR_ROTATION) {
                double[] iterationResponse = controlLoopCalculator.iteration(inputRecord.getY(), inputRecord.getX(), inputRecord.getTagAngle(), -Constants.ROTATE_SPEED);
                outputRecord.setMotorVelocityRatio(iterationResponse[0]);
                if (iterationResponse[1] < 0 && iterationResponse[2] < 0 ){
                    outputRecord.setBothMotorsNegativeForRotation(true);
                } else {
                    outputRecord.setBothMotorsNegativeForRotation(false);
                }
            }
        }
        return outputRecord;
    }

    public AgvOutputsRecord runOnFinalAngle(AgvOutputsRecord outputRecord, RotationRecord rotationRecord) throws InterruptedException {
        logger.debug("On final angle");
        int counter = rotationRecord.getOnTargetAngleCounter();
        if (counter >= Constants.ON_TARGET_ANGLE_COUNTER) {
            logger.debug("Rotation finished");
            outputRecord.setDirection(Constants.STOP_STRING);
            outputRecord.setMotorVelocityRatio(0);
            logger.debug("Setting AGV to stop");
            this.rotationRecord = new RotationRecord(); //resets the on angle counter to 0 and the started rotation to false
            controlLoopCalculator.resetTerms();
            outputRecord.setRotationIsFinished(true);
        } else {
            logger.debug("Incrementing final angle counter");
            rotationRecord.setOnTargetAngleCounter(++counter);
        }
        return outputRecord;
    }

    public RotationRecord getRotationRecord(){
        return this.rotationRecord;
    }

    public  void setRotationRecord(RotationRecord rotationRecord){
        this.rotationRecord = rotationRecord;
    }

    public void setControlLoopCalculator(ControlLoop controlLoopCalculator) {
        this.controlLoopCalculator = controlLoopCalculator;
    }

    public void setRotationType(int rotationType) {
        this.rotationType = rotationType;
    }
}
