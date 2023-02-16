package com.mochilafulfillment.server.position_scanner;

import com.mochilafulfillment.server.agv_utils.ByteToolbox;
import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.agv_utils.Exceptions.PGVException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.Position;

import static com.mochilafulfillment.server.agv_utils.ByteToolbox.*;

public abstract class Response {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public abstract void process(int[] responseTelegram);

    public double angle(int[] responseTelegram) throws PGVException {
        int byte11 = responseTelegram[10 + PositionScannerConstants.REQUEST_DIRECTION_BYTE_LENGTH];
        int byte12 = responseTelegram[11 + PositionScannerConstants.REQUEST_DIRECTION_BYTE_LENGTH];
        int byteSum = byteArrayToInt(new int[] {byte11, byte12},PositionScannerConstants.BITS_PER_BYTE);
        double angle = (byteSum + PositionScannerConstants.ANGLE_OFFSET) / 10.; //angle resolution is .1 degrees
        logger.debug("The angle read is " + angle);
        if(Math.abs(angle) >= 360){
            logger.error("Error: Angle read by position scanner is: " + angle);
            throw new PGVException("Angle read by PGV is greater than 360 degrees");
        }
        return angle;
    }

    public double yPosition(int[] responseTelegram) throws PGVException {
        int byte8 = responseTelegram[7 + PositionScannerConstants.REQUEST_DIRECTION_BYTE_LENGTH];
        int byte7 = responseTelegram[6 + PositionScannerConstants.REQUEST_DIRECTION_BYTE_LENGTH];

        int yPositionData = ByteToolbox.byteArrayToInt(new int[] {byte7, byte8}, PositionScannerConstants.BITS_PER_BYTE);
        double convertedY = signBits(yPositionData, 14) / 10.; //y-position resolution is .1 degrees
        logger.debug("y position is " + convertedY);
//        System.out.println("yposition: " + convertedY);
        if(Math.abs(convertedY) > PositionScannerConstants.MAX_Y_POSITION){
            logger.error("Error: Y-position magnitude read by position scanner is " + convertedY);
            throw new PGVException("Y-position magnitude read by position scanner is greater than " + + PositionScannerConstants.MAX_Y_POSITION);
        }

        return (convertedY);
    }
}