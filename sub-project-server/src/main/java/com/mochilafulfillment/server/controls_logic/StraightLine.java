package com.mochilafulfillment.server.controls_logic;

import com.mochilafulfillment.server.controls_logic.utils.ControlLoop;
import com.mochilafulfillment.server.dtos.AgvOutputsRecord;
import com.mochilafulfillment.server.dtos.AgvInputsRecord;
import com.mochilafulfillment.server.controls_logic.dtos.StraightLineRecord;
import com.mochilafulfillment.server.agv_utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StraightLine{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    StraightLineRecord straightLineRecord = new StraightLineRecord();
    private final ControlLoop controlLoopCalculator;

    public StraightLine(ControlLoop controlLoopCalculator) {
        this.controlLoopCalculator = controlLoopCalculator;
    }

    public AgvOutputsRecord run(AgvOutputsRecord outputRecord, AgvInputsRecord inputRecord) {
        //Straight line mode will run if direction is forwards or backwards but the tag cannot be finished. This is because
        //after the tag is finished all that has to happen is rotating and lifting before the end of the command line.
        // In LQR rotation mode, the direction is either forwards or backwards but we do not want straight line mode to run
        if((outputRecord.getDirection() == Constants.FORWARDS_STRING || outputRecord.getDirection() == Constants.BACKWARDS_STRING) && outputRecord.getTagIsFinished() == false){
            logger.debug("AGV is in straight line mode");
            if (!inputRecord.isOnTape() && inputRecord.getTagId() == 0) {
                int counter = straightLineRecord.getZeroYCounts();
                if (counter == 0) {
                    logger.warn("AGV is not on tape");
                }
                if (counter < Constants.ZERO_Y_COUNTS_CUTOFF) {
                    straightLineRecord.setZeroYCounts(++counter);
                } else {
                    outputRecord.setNominalVelocity(0);
                    outputRecord.setDirection(Constants.STOP_STRING);
                    logger.error("Setting motor velocities to 0 because off the line for too long");
                }
            } else if(straightLineRecord.getZeroYCounts() != 0 && Math.abs(inputRecord.getY()) > Constants.BACK_ON_TAPE_Y_POSITION_MINIMUM){
                logger.info("AGV found tape after " +  straightLineRecord.getZeroYCounts() + " counts");
                straightLineRecord.setZeroYCounts(0);
            } else if(outputRecord.getNominalVelocity() != 0 ){
                if(straightLineRecord.getZeroYCounts() == 0) {
                    double[] motorVelocityRatio = controlLoopCalculator.iteration(inputRecord.getY(), inputRecord.getX(), inputRecord.getTapeAngle(), outputRecord.getNominalVelocity());
                    outputRecord.setMotorVelocityRatio(motorVelocityRatio[0]);
                } else {
                    logger.error("Error: PGV returned a faulty read when off of the line");
                }
            } else {
                logger.error("Error: In straight line mode but nominal velocity is 0");
            }
        } else {
            logger.debug("Direction not set to forwards or backwards");
        }
        return(outputRecord);
    }
}