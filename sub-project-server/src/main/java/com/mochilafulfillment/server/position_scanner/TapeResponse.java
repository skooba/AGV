package com.mochilafulfillment.server.position_scanner;
import com.mochilafulfillment.server.agv_utils.Exceptions.PGVException;
import com.mochilafulfillment.server.position_scanner.dtos.PositionScannerResponseRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mochilafulfillment.server.agv_utils.ByteToolbox.isBitInInt;

public class TapeResponse extends Response {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    PositionScannerResponseRecord positionScannerRecord;
    public TapeResponse(PositionScannerResponseRecord positionScannerRecord){
        this.positionScannerRecord = positionScannerRecord;
    }

    @Override
    public void process(int[] responseTelegram) {
        positionScannerRecord.setXPosition(0);//no x-position if in lane tracking mode
        positionScannerRecord.setTagId(0); // tagID 0 if in lane tracking
        if(laneFound(responseTelegram)) {
            try {
                double y = yPosition(responseTelegram);
                double angle = angle(responseTelegram);
                positionScannerRecord.setTapeAngle(angle);
                positionScannerRecord.setYPosition(y);
            } catch (PGVException e){
                logger.debug("Not changing scanner angle or y-position");
            }
            positionScannerRecord.setOnTape(true);
        } else {
            positionScannerRecord.setOnTape(false); // don't change the y position
        }
    }

    public boolean laneFound(int[] responseTelegram) {
        int laneFoundByte = responseTelegram[1 + PositionScannerConstants.REQUEST_DIRECTION_BYTE_LENGTH];
        boolean lookForColoredTape = !isBitInInt(laneFoundByte,2); // Byte 2, Bit 2 -> No lane detected if 1
        if (lookForColoredTape) {
            logger.debug("Lane found");
            return true;
        }
        logger.debug("Lane not found");
        return false;
    }

    public void setPositionScannerRecord(PositionScannerResponseRecord positionScannerRecord){
        this.positionScannerRecord = positionScannerRecord;

    }
}
