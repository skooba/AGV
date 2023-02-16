package com.mochilafulfillment.server.position_scanner;


import com.mochilafulfillment.server.agv_utils.ByteToolbox;
import com.mochilafulfillment.server.agv_utils.Exceptions.PGVException;
import com.mochilafulfillment.server.position_scanner.dtos.PositionScannerResponseRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mochilafulfillment.server.agv_utils.ByteToolbox.signBits;

public class TagResponse extends Response{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private PositionScannerResponseRecord positionScannerRecord;

    public TagResponse(PositionScannerResponseRecord positionScannerRecord){
        this.positionScannerRecord = positionScannerRecord;
    }

    @Override
    public void process(int[] responseTelegram) {
        int tagId = tagId(responseTelegram); // returns a number between 0 and 268435456 representing the tag ID
        positionScannerRecord.setTagId(tagId);
        if (isGridRepresentation(responseTelegram)){
            int columns = gridColumns(responseTelegram);
            int rows = gridRows(responseTelegram);
            positionScannerRecord.setColumns(columns);
            positionScannerRecord.setRows(rows);
        } else {
            positionScannerRecord.setColumns(0);
            positionScannerRecord.setRows(0);
        }

        // If the tag ID is 0 then AGV is not on the tape
        if (tagId == 0) {
            logger.error("Error: In tag mode but tag ID is 0");

        } else if (tagId > 0) {
            try {
                double angle = angle(responseTelegram);
                double absoluteXPosition = xPosition(responseTelegram);
                double absoluteYPosition = yPosition(responseTelegram);

                // PGV is configured to send 1000 if there is no position found on Tag and a 1001 if there is an error
                if(angle == 1000 || absoluteXPosition == 1000 || absoluteYPosition == 1000 || angle == 1001 || absoluteXPosition == 1001 || absoluteYPosition == 1001) {
                    if(angle == 1000 || absoluteXPosition == 1000 || absoluteYPosition == 1000) {
                        logger.error("Error: no position value found for x, y or error");
                        throw new PGVException("No position value received from PGV");
                    } else {
                        logger.error("Error: error value found for x, y or error");
                        throw new PGVException("Error value received from PGV");
                    }
                } else {
                    logger.debug("valid response from PGV scanner");
                    positionScannerRecord.setTagAngle(angle);
                    //If AGV rolls onto a tag from the front then positive y is on the tag's left side and positive x is the tag's up-side
                    //If AGV rolls onto a tag from the back then positive y is on the tag's right side and positive x is the tag's down-side
                    //If AGV rolls onto tag from the right then positive y is on the tag's down-side and positive x is the tag's right side
                    //If AGV rolls onto tag from the left then positive y is on the tag's up-side and positive x is the tag's left side
                    // Note: considering (1,1) on grid corresponds to bottom right of grid, the tags are laid out with positive x direction pointing up and positive y direction pointing to the left
                    if (angle > 45. && angle <= 135.) {
                        logger.debug("Approaching tag ID from right");
//                    System.out.println("RIGHT");
                        positionScannerRecord.setYPosition(-absoluteXPosition);
                        positionScannerRecord.setXPosition(absoluteYPosition);
                        positionScannerRecord.setTapeAngle(angle - 90.);
                    } else if (angle > 225. && angle <= 315.) {
                        logger.debug("Approaching tag ID from left");
//                    System.out.println("LEFT");
                        positionScannerRecord.setYPosition(absoluteXPosition);
                        positionScannerRecord.setXPosition(-absoluteYPosition);
                        positionScannerRecord.setTapeAngle(angle - 270.);
                    } else if (angle > 315. || angle <= 45.) {
                        logger.debug("Approaching tag ID from back");
//                    System.out.println("BACK");
                        positionScannerRecord.setYPosition(-absoluteYPosition);
                        positionScannerRecord.setXPosition(-absoluteXPosition);
                        positionScannerRecord.setTapeAngle(angle);
                    } else {
                        logger.debug("Approaching tag from front");
//                        System.out.println("FRONT");
                        positionScannerRecord.setYPosition(absoluteYPosition);
                        positionScannerRecord.setXPosition(absoluteXPosition);
                        positionScannerRecord.setTapeAngle(angle - 180.);
                    }
                }
            } catch(PGVException e){
                logger.debug("Not changing scanner angle or scanner y-position");
            }

        } else {
            throw new IllegalArgumentException("unknown tagId value");
        }

    }

    public double xPosition(int[] responseTelegram) {

        int byte3 = responseTelegram[2 + PositionScannerConstants.REQUEST_DIRECTION_BYTE_LENGTH];
        int byte4 = responseTelegram[3 + PositionScannerConstants.REQUEST_DIRECTION_BYTE_LENGTH];
        int byte5 = responseTelegram[4 + PositionScannerConstants.REQUEST_DIRECTION_BYTE_LENGTH];
        int byte6 = responseTelegram[5 + PositionScannerConstants.REQUEST_DIRECTION_BYTE_LENGTH];

        int xPositionBitsInByte3 = 3;

        int xPositionData = ByteToolbox.byteArrayToInt(new int[] {byte4, byte5, byte6}, PositionScannerConstants.BITS_PER_BYTE);
        int threeBitMask = (int)(Math.pow(2,xPositionBitsInByte3)-1); //00000111
        int byte3XPositionData = (byte3 & threeBitMask) << 21;

        xPositionData = xPositionData | byte3XPositionData; //shift bytes 4-6 to the left to make room for additional 3 bits

        double convertedX = signBits(xPositionData, 24)/10.;

        logger.debug("x position is " + convertedX);


        return convertedX;
    }

    public int tagId(int[] responseTelegram) {// Get tag location
        int firstTagByte, secondTagByte, thirdTagByte, fourthTagByte;

        firstTagByte = responseTelegram[17 + PositionScannerConstants.REQUEST_DIRECTION_BYTE_LENGTH];
        secondTagByte = responseTelegram[16 + PositionScannerConstants.REQUEST_DIRECTION_BYTE_LENGTH];
        thirdTagByte = responseTelegram[15 + PositionScannerConstants.REQUEST_DIRECTION_BYTE_LENGTH];
        fourthTagByte = responseTelegram[14 + PositionScannerConstants.REQUEST_DIRECTION_BYTE_LENGTH];
        int[] tagBytes = {fourthTagByte, thirdTagByte, secondTagByte, firstTagByte};

        int tagByteSum = ByteToolbox.byteArrayToInt(tagBytes, PositionScannerConstants.BITS_PER_BYTE);

        logger.debug("Tag ID read from scanner is " + tagByteSum);

        return tagByteSum; // returns a number between 0 and 268435456 representing the tag ID
    }

    public boolean isGridRepresentation(int[] responseTelegram){
        int thirdTagByte;
        thirdTagByte = responseTelegram[15 + PositionScannerConstants.REQUEST_DIRECTION_BYTE_LENGTH];
        boolean isGridRepresentation = !ByteToolbox.isBitInInt(thirdTagByte,0);
        if(isGridRepresentation){
            logger.debug("AGV is on a grid representation tag");
        } else {
            logger.debug("AGV is not on a grid representation tag");
        }
        return isGridRepresentation;
    }

    public int gridColumns(int[] responseTelegram) {
        int firstTagByte;
        firstTagByte = responseTelegram[17 + PositionScannerConstants.REQUEST_DIRECTION_BYTE_LENGTH];
        logger.debug("Grid column number is " + firstTagByte);
        return firstTagByte;
    }

    public int gridRows(int[] responseTelegram) {
        int secondTagByte;
        secondTagByte = responseTelegram[16 + PositionScannerConstants.REQUEST_DIRECTION_BYTE_LENGTH];
        logger.debug("Grid row number is " + secondTagByte);
        return secondTagByte;
    }

    public PositionScannerResponseRecord getPositionScannerRecord() {
        return positionScannerRecord;
    }

    public void setPositionScannerRecord(PositionScannerResponseRecord positionScannerRecord){
        this.positionScannerRecord = positionScannerRecord;
    }
}