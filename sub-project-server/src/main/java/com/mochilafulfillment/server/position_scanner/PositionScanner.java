package com.mochilafulfillment.server.position_scanner;

import com.mochilafulfillment.server.agv_utils.ByteToolbox;
import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.position_scanner.dtos.PositionScannerResponseRecord;
import com.mochilafulfillment.server.communications.serial_port.PortHandler;
import jssc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class PositionScanner implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private PortHandler portHandler;
    public final PositionScannerResponseRecord positionScannerRecord;

    public PositionScanner(String serialPortName, PositionScannerResponseRecord positionScannerRecord) {
        logger.debug("Position Scanner port name is " + serialPortName);
        this.portHandler = new PortHandler(new SerialPort(serialPortName));
        this.positionScannerRecord = positionScannerRecord;
    }

    public void run() {
        try {
            portHandler.run(PositionScannerConstants.BAUD_RATE, PositionScannerConstants.PARITY);

            while (true) { // goes until thread-pool is shutdown in RunProgram

                if (!positionScannerRecord.isNewPositionScannerRecord()) { // waits until this is set to false in AutomaticMode
                    makeRequest();
                    int[] responseTelegram = receiveResponse();
                    logger.debug("New response received from the position scanner :" + Arrays.toString(responseTelegram));
                    if (responseTelegram != null) {
                        processResponse(responseTelegram);
                        positionScannerRecord.setNewPositionScannerRecord(true); //used in AutomaticMode
                    }
                    else {
//                        logger.warn("Null response from scanner");
//                        throw new NullPointerException("Error: received null response from position scanner");
                    }
                }
                Thread.sleep(Constants.FAST_LOOP_PAUSE_TIME);
            }
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                throw new RuntimeException("wrapped InterruptedException", e);
            } else if (e instanceof SerialPortTimeoutException) {
                throw new RuntimeException("wrapped SerialPortTimeoutException", e);
            } else if (e instanceof SerialPortException) {
                throw new RuntimeException("wrapped SerialPortException", e);
            }
        }
    }

    public void makeRequest() throws SerialPortException {

        synchronized (portHandler) {
            if (portHandler.writeSerialPortBytes(PositionScannerConstants.REQUEST_LEFT_LANE) &&
                    portHandler.writeSerialPortBytes(PositionScannerConstants.REQUEST_TELEGRAM)
            ){
              logger.debug("Request successfully sent to position scanner");
            } else {
                throw new SerialPortException(portHandler.getSerialPort().getPortName(), "run", "Unsuccessful request sent to serial port " + portHandler.getSerialPort().getPortName());
            }
        }
    }

    public int[] receiveResponse() {
        int responseLength = PositionScannerConstants.REQUEST_DIRECTION_BYTE_LENGTH + PositionScannerConstants.REQUEST_POSITION_BYTE_LENGTH;
        synchronized (portHandler) {
            int[] responseTelegram;
            try {
                responseTelegram = portHandler.readSerialPortBytes(responseLength, true, Constants.POSITION_SCANNER_TIMEOUT);
            } catch (SerialPortTimeoutException e ){
                logger.error("Error: Serial port timeout exception");
                return null;
            } catch (SerialPortException e){
                logger.error("Error: PGV serial port exception");
                return null;
            }
            return responseTelegram;
        }
    }

    public String processResponse(int[] responseTelegram) {
        if(isOnTag(responseTelegram[1 + PositionScannerConstants.REQUEST_DIRECTION_BYTE_LENGTH]) == true){
            logger.debug("Tag mode");
            TagResponse tagResponse = new TagResponse(positionScannerRecord);
            tagResponse.process(responseTelegram);
            return "Tag mode";
        } else {
            logger.debug("Tape mode");
            TapeResponse tapeResponse = new TapeResponse(positionScannerRecord);
            tapeResponse.process(responseTelegram);
            return "Tape mode";
        }
    }

    public PositionScannerResponseRecord getPositionScannerResponseRecord() {
        return positionScannerRecord;
    }

    public boolean isOnTag(int onTagByte) {
        if(ByteToolbox.isBitInInt(onTagByte,6)){
            logger.debug("On tag");
            return true;
        } else {
            logger.debug("On tape");
            return false;
        }
    }

    public void setPortHandler(PortHandler portHandler){
        this.portHandler = portHandler;
    }
}