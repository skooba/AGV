package com.mochilafulfillment.server.controls_logic;

import com.mochilafulfillment.server.agv_utils.ByteToolbox;
import com.mochilafulfillment.server.agv_utils.Exceptions.PGVException;
import com.mochilafulfillment.server.dtos.AgvOutputsRecord;
import com.mochilafulfillment.server.dtos.AgvInputsRecord;
import com.mochilafulfillment.server.controls_logic.dtos.PathRecord;
import com.mochilafulfillment.server.controls_logic.dtos.StopOnTagRecord;
import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.position_scanner.PositionScannerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StopOnTag{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private StopOnTagRecord stopOnTagRecord = new StopOnTagRecord();


    public AgvOutputsRecord run(AgvOutputsRecord outputRecord, AgvInputsRecord agvInputsRecord, PathRecord pathRecord) throws PGVException, InterruptedException {
        if (stopOnTagRecord.isFirstPass() == true){
            stopOnTagRecord.setNextId(pathRecord.getTagId()); //From the CSV file
            logger.debug("The destination tag's ID is " + pathRecord.getTagId());
            stopOnTagRecord.setFirstPass(false);
        }

        int tagId = agvInputsRecord.getTagId();
        int columnNumber = agvInputsRecord.getColumnNumber();
        int rowNumber = agvInputsRecord.getRowNumber();
        double xPosition = agvInputsRecord.getX();
        if (tagId != 0) {
            logger.debug("On tag with tag ID = " + tagId + " and x position = " + xPosition);
            OnTag onTag = new OnTag();
            outputRecord = onTag.run(outputRecord, tagId, columnNumber, rowNumber, xPosition); // changes  to output record
        } else {// If AGV is not on a tag
            if(outputRecord.getDirection() != Constants.STOP_STRING){
                outputRecord.setNominalAccel(Constants.STANDARD_ACCELERATION);
            }
            logger.debug("AGV not on a tag, Tag ID=0");
        }
        return outputRecord;
    }

    public int traversingColumns(int destinationColumn, int destinationRow, int currentColumn, int currentRow) {
        int numberOfColumnsNeededToTraverse;
        if (currentRow == destinationRow && currentColumn != destinationColumn) {
            logger.debug("AGV is Traversing Columns");
            numberOfColumnsNeededToTraverse = destinationColumn - currentColumn;
            return Math.abs(numberOfColumnsNeededToTraverse);
        } else {
            return -1;
        }
    }

    public int traversingRows(int destinationColumn, int destinationRow, int currentColumn, int currentRow) {
        int numberOfRowsNeededToTraverse;
        if (currentColumn == destinationColumn && currentRow != destinationRow) {
            logger.debug("AGV is Traversing Rows");
            numberOfRowsNeededToTraverse = destinationRow - currentRow;
            return Math.abs(numberOfRowsNeededToTraverse);
        } else {
            return -1;
        }
    }

    class OnTag {
        AgvOutputsRecord run(AgvOutputsRecord outputRecord, int tagId, int columnNumber, int rowNumber, double xPosition) throws PGVException, InterruptedException {
            int destinationID = stopOnTagRecord.getNextId();

            if (stopOnTagRecord.getLastId() != tagId) {
                logger.debug("This is the first read of a new tag");
                stopOnTagRecord.setLastId(tagId);
            }
            if (tagId == destinationID) {
                logger.debug("AGV is on stop tag");
                stopOnTagRecord.setOneTagAway(false);
                return onStopTag(outputRecord, xPosition);
            } else {
                int tagsAway = howManyTagsAway(destinationID, columnNumber, rowNumber);
                if (tagsAway == 1) {
                    if (stopOnTagRecord.isOneTagAway()) {
                        logger.debug("Not first time reading the one tag away tag");
                    } else {
                        stopOnTagRecord.setOneTagAway(true);
                        logger.debug("Setting one tag away velocity and acceleration");
                        outputRecord.setNominalAccel(Constants.ONE_TAG_AWAY_ACCEL);
                        outputRecord.setNominalVelocity(Constants.ONE_TAG_AWAY_VELOCITY);
                    }
                    return outputRecord;
                }
                return outputRecord;
            }
        }

        int howManyTagsAway(int destinationID, int currentColumn, int currentRow){
            if(ByteToolbox.isBitInInt(destinationID, PositionScannerConstants.GRID_REPRESENTATION_TAG_ID_BIT)){ // Set to 0 in Tag Response if not on grid representation
                logger.error("Error: Not on grid representation");
                return -1;
            }
            int destinationColumn =  ByteToolbox.intToByteArray(destinationID, PositionScannerConstants.BITS_PER_BYTE)[3];
            logger.debug("Destination column is " + destinationColumn);
            int destinationRow =  ByteToolbox.intToByteArray(destinationID, PositionScannerConstants.BITS_PER_BYTE)[2];
            logger.debug("Destination row is " + destinationRow);
            int numberOfColumnsNeededToTraverseAndDirection = traversingColumns(destinationColumn, destinationRow, currentColumn, currentRow);
            logger.debug("Number of columns needed to traverse is " + numberOfColumnsNeededToTraverseAndDirection);
            int numberOfRowsNeededToTraverseAndDirection = traversingRows(destinationColumn, destinationRow, currentColumn, currentRow);
            logger.debug("Number of rows needed to traverse is " + numberOfRowsNeededToTraverseAndDirection);

            if(numberOfColumnsNeededToTraverseAndDirection != -1){ //Columns are first byte in tagID
                logger.debug("AGV is " + numberOfColumnsNeededToTraverseAndDirection + " number of tags away from stop tag");
                return numberOfColumnsNeededToTraverseAndDirection;
            }
            else if(numberOfRowsNeededToTraverseAndDirection != -1){
                logger.debug("AGV is " + numberOfRowsNeededToTraverseAndDirection + " number of tags away from stop tag");
                return numberOfRowsNeededToTraverseAndDirection;
            }
            logger.error("Error: Neither row or column align between destination and current tag");
            return -1;
        }

        AgvOutputsRecord onStopTag(AgvOutputsRecord outputRecord, double xPosition) throws PGVException, InterruptedException {
            if(stopOnTagRecord.getXCounter() == 0) {
                outputRecord.setNominalVelocity(Constants.ON_STOP_TAG_VELOCITY);
                outputRecord.setNominalAccel(Constants.ON_STOP_TAG_ACCEL);
                logger.debug("Velocity and accel set to stop Tag velocity");
            }
            if (xPosition < Constants.X_POSITION_STOP_OFFSET) {
                int counter = stopOnTagRecord.getXCounter();
                if (counter >= Constants.ZERO_X_COUNTS_CUTOFF) {
                    logger.error("Error: Did not find center of tag, setting velocity to 0");
                    outputRecord.setDirection(Constants.STOP_STRING);
                    outputRecord.setNominalAccel(Constants.STOPPING_ACCELERATION);
                    throw new PGVException("PGV tried to find center of tag but could not, try increasing 'zero x counts cutoff'");
                }
                stopOnTagRecord.setXCounter(++counter);
            } else {
                logger.debug("Center of tag found at " + xPosition +"; AGV is stopping");
                outputRecord.setDirection(Constants.STOP_STRING);
                outputRecord.setTagIsFinished(true);
                stopOnTagRecord = new StopOnTagRecord();
            }
            return outputRecord; // no changes made unless stopped
        }
    }

    public StopOnTagRecord getStopOnTagRecord(){
        return this.stopOnTagRecord;
    }

    public void setStopOnTagRecord(StopOnTagRecord stopOnTagRecord){
        this.stopOnTagRecord = stopOnTagRecord;
    }

}

