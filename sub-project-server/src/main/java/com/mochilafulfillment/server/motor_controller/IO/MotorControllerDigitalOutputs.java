package com.mochilafulfillment.server.motor_controller.IO;

import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.motor_controller.MotorControllerConstants;
import com.mochilafulfillment.server.motor_controller.MotorOutputCommand;
import com.mochilafulfillment.server.motor_controller.dtos.MotorControllerRecord;
import com.mochilafulfillment.server.communications.serial_port.PortHandler;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class MotorControllerDigitalOutputs {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private MotorOutputCommand motorControllerWrite;

    private static final byte byteLengthDigitalOutput = 8;


    public MotorControllerDigitalOutputs(PortHandler portHandler) {
        this.motorControllerWrite = new MotorOutputCommand(portHandler);
    }

    public void write(MotorControllerRecord controllerOutputRecord, MotorControllerRecord oldControllerOutputRecord) throws SerialPortException, SerialPortTimeoutException, InterruptedException {

        int liftType = controllerOutputRecord.getLiftType(); // 0 =stop, 1=lift, 2=lower
        int scannerType = controllerOutputRecord.getSafetyScannerMode();

        boolean compareLiftType = liftType  == oldControllerOutputRecord.getLiftType();
        boolean compareScanType = scannerType  == oldControllerOutputRecord.getSafetyScannerMode();

        //Set the lift
        if(!compareLiftType) {
            if (liftType == 0) {
                logger.debug("Stop forklift movement");
                setLiftStop();
            } else if (liftType == 1) {
                logger.debug("Forklift moving up");
                setLiftUp();
            } else if (liftType == 2) {
                logger.debug("Forklift moving down");
                setLiftDown();
            } else {
                logger.error("Error: Lift type can only be type 1 or 2");
                throw new IllegalArgumentException();
            }
        }

        //Set the safety scanner
        if(!compareScanType) {
            if (scannerType == Constants.REGULAR_SCAN) {
                logger.debug("Safety scanner in normal mode");
                setScannerNormal();
            } else if (scannerType == Constants.PICKING_SCAN) {
                logger.debug("Safety scanner in picking mode");
                setScannerPicking();
            } else {
                logger.error("Error: Scanner type can only be type 1 or 2");
                throw new IllegalArgumentException();
            }
        }
    }

    public void setLiftUp() throws SerialPortException, SerialPortTimeoutException, InterruptedException {
        motorControllerWrite.run(createLiftDownStopBytes(),1);
        motorControllerWrite.run(createLiftUpBytes(),1);
    }

    public void setLiftDown() throws SerialPortException, SerialPortTimeoutException, InterruptedException {
        motorControllerWrite.run(createLiftUpStopBytes(),1);
        motorControllerWrite.run(createLiftDownBytes(),1);
    }

    public void setLiftStop() throws SerialPortException, SerialPortTimeoutException, InterruptedException {
        motorControllerWrite.run(createLiftUpStopBytes(),1); //setting this to low will cause red light to turn on in motor controller
        motorControllerWrite.run(createLiftDownStopBytes(),1);
    }

    public void setScannerNormal() throws SerialPortException, SerialPortTimeoutException, InterruptedException {
        motorControllerWrite.run(createScanner1On(), 1);
        motorControllerWrite.run(createScanner2Off(),1);
    }

    public void setScannerPicking() throws SerialPortException, SerialPortTimeoutException, InterruptedException {
        motorControllerWrite.run(createScanner1Off(), 1);
        motorControllerWrite.run(createScanner2On(), 1);
    }

    public int[] createLiftUpBytes() {
        int[] liftUpBytes = new int[]{byteLengthDigitalOutput, MotorControllerConstants.AXIS_2_ID_HIGH, MotorControllerConstants.AXIS_2_ID_LOW,
                MotorControllerConstants.COMMAND_OUTPUT_CODE_HIGH, MotorControllerConstants.COMMAND_OUTPUT_CODE_LOW, MotorControllerConstants.OUTPUT_2_MESSAGE_HIGH,
                MotorControllerConstants.OUTPUT_2_MESSAGE_LOW, MotorControllerConstants.OUTPUT_OFF_MESSAGE_HIGH, MotorControllerConstants.OUTPUT_OFF_MESSAGE_LOW, 0};
        int checkSumLiftUp = IOUtils.checkSum(liftUpBytes);
        liftUpBytes[byteLengthDigitalOutput + 1] = checkSumLiftUp;
        return liftUpBytes;
    }

    public int[] createLiftUpStopBytes() {
        int[] liftUpStopBytes = new int[]{byteLengthDigitalOutput, MotorControllerConstants.AXIS_2_ID_HIGH, MotorControllerConstants.AXIS_2_ID_LOW,
                MotorControllerConstants.COMMAND_OUTPUT_CODE_HIGH, MotorControllerConstants.COMMAND_OUTPUT_CODE_LOW, MotorControllerConstants.OUTPUT_2_MESSAGE_HIGH,
                MotorControllerConstants.OUTPUT_2_MESSAGE_LOW, MotorControllerConstants.OUTPUT_2_MESSAGE_HIGH, MotorControllerConstants.OUTPUT_2_MESSAGE_LOW, 0};
        int checkSumLiftUpStop = IOUtils.checkSum(liftUpStopBytes);
        liftUpStopBytes[byteLengthDigitalOutput + 1] = checkSumLiftUpStop;
        return liftUpStopBytes;
    }

    public int[] createLiftDownBytes() {
        int[] liftDownBytes = new int[]{byteLengthDigitalOutput, MotorControllerConstants.AXIS_2_ID_HIGH, MotorControllerConstants.AXIS_2_ID_LOW,
                MotorControllerConstants.COMMAND_OUTPUT_CODE_HIGH, MotorControllerConstants.COMMAND_OUTPUT_CODE_LOW, MotorControllerConstants.OUTPUT_3_MESSAGE_HIGH,
                MotorControllerConstants.OUTPUT_3_MESSAGE_LOW, MotorControllerConstants.OUTPUT_OFF_MESSAGE_HIGH, MotorControllerConstants.OUTPUT_OFF_MESSAGE_LOW, 0};
        int checkSumLiftDown = IOUtils.checkSum(liftDownBytes);
        liftDownBytes[byteLengthDigitalOutput + 1] = checkSumLiftDown;
        return liftDownBytes;
    }

    public int[] createLiftDownStopBytes() {
        int[] liftDownStopBytes = new int[]{byteLengthDigitalOutput, MotorControllerConstants.AXIS_2_ID_HIGH, MotorControllerConstants.AXIS_2_ID_LOW,
                MotorControllerConstants.COMMAND_OUTPUT_CODE_HIGH, MotorControllerConstants.COMMAND_OUTPUT_CODE_LOW, MotorControllerConstants.OUTPUT_3_MESSAGE_HIGH,
                MotorControllerConstants.OUTPUT_3_MESSAGE_LOW, MotorControllerConstants.OUTPUT_3_MESSAGE_HIGH, MotorControllerConstants.OUTPUT_3_MESSAGE_LOW, 0};
        int checkSumLiftDownStop = IOUtils.checkSum(liftDownStopBytes);
        liftDownStopBytes[byteLengthDigitalOutput + 1] = checkSumLiftDownStop;
        return liftDownStopBytes;
    }


    public int[] createScanner1On() {
        int[] scannerOutputBytes1High = new int[]{byteLengthDigitalOutput, MotorControllerConstants.AXIS_1_ID_HIGH, MotorControllerConstants.AXIS_1_ID_LOW,
                MotorControllerConstants.COMMAND_OUTPUT_CODE_HIGH, MotorControllerConstants.COMMAND_OUTPUT_CODE_LOW, MotorControllerConstants.OUTPUT_2_MESSAGE_HIGH,
                MotorControllerConstants.OUTPUT_2_MESSAGE_LOW, MotorControllerConstants.OUTPUT_2_MESSAGE_HIGH, MotorControllerConstants.OUTPUT_2_MESSAGE_LOW, 0};
        int checkSumScannerOutputBytes1High = IOUtils.checkSum(scannerOutputBytes1High);
        scannerOutputBytes1High[byteLengthDigitalOutput + 1] = checkSumScannerOutputBytes1High;

        return scannerOutputBytes1High;
    }

    public int[] createScanner1Off() {
        int[] scannerOutputBytes1Low = new int[]{byteLengthDigitalOutput, MotorControllerConstants.AXIS_1_ID_HIGH, MotorControllerConstants.AXIS_1_ID_LOW,
                MotorControllerConstants.COMMAND_OUTPUT_CODE_HIGH, MotorControllerConstants.COMMAND_OUTPUT_CODE_LOW, MotorControllerConstants.OUTPUT_2_MESSAGE_HIGH,
                MotorControllerConstants.OUTPUT_2_MESSAGE_LOW, MotorControllerConstants.OUTPUT_OFF_MESSAGE_HIGH, MotorControllerConstants.OUTPUT_OFF_MESSAGE_LOW, 0};
        int checkScannerOutputBytes1Low = IOUtils.checkSum(scannerOutputBytes1Low);
        scannerOutputBytes1Low[byteLengthDigitalOutput + 1] = checkScannerOutputBytes1Low;
        return scannerOutputBytes1Low;
    }

    public int[] createScanner2On() {
        int[] scannerOutputBytes2High = new int[]{byteLengthDigitalOutput, MotorControllerConstants.AXIS_1_ID_HIGH, MotorControllerConstants.AXIS_1_ID_LOW,
                MotorControllerConstants.COMMAND_OUTPUT_CODE_HIGH, MotorControllerConstants.COMMAND_OUTPUT_CODE_LOW, MotorControllerConstants.OUTPUT_3_MESSAGE_HIGH,
                MotorControllerConstants.OUTPUT_3_MESSAGE_LOW, MotorControllerConstants.OUTPUT_3_MESSAGE_HIGH, MotorControllerConstants.OUTPUT_3_MESSAGE_LOW, 0};
        int checkSumScannerOutputBytes1High = IOUtils.checkSum(scannerOutputBytes2High);
        scannerOutputBytes2High[byteLengthDigitalOutput + 1] = checkSumScannerOutputBytes1High;
        return scannerOutputBytes2High;
    }

    public int[] createScanner2Off() {
        int[] scannerOutputBytes1Low = new int[]{byteLengthDigitalOutput, MotorControllerConstants.AXIS_1_ID_HIGH, MotorControllerConstants.AXIS_1_ID_LOW,
                MotorControllerConstants.COMMAND_OUTPUT_CODE_HIGH, MotorControllerConstants.COMMAND_OUTPUT_CODE_LOW, MotorControllerConstants.OUTPUT_3_MESSAGE_HIGH,
                MotorControllerConstants.OUTPUT_3_MESSAGE_LOW, MotorControllerConstants.OUTPUT_OFF_MESSAGE_HIGH, MotorControllerConstants.OUTPUT_OFF_MESSAGE_LOW, 0};
        int checkScannerOutputBytes1Low = IOUtils.checkSum(scannerOutputBytes1Low);
        scannerOutputBytes1Low[byteLengthDigitalOutput + 1] = checkScannerOutputBytes1Low;
        return scannerOutputBytes1Low;
    }

    public void setMotorControllerWrite(MotorOutputCommand motorControllerWrite){
        this.motorControllerWrite = motorControllerWrite;
    }
}
