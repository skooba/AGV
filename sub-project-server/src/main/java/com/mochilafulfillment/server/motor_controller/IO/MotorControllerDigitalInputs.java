package com.mochilafulfillment.server.motor_controller.IO;

import com.mochilafulfillment.server.agv_utils.ByteToolbox;
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
import java.util.concurrent.locks.ReentrantLock;


// Read all inputs on the controller
public class MotorControllerDigitalInputs {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MotorOutputCommand motorControllerWrite;
    private final static int byteLengthInputQuery = 8;

    public MotorControllerDigitalInputs(PortHandler portHandler) {
        this.motorControllerWrite = new MotorOutputCommand(portHandler);
    }

    public MotorControllerRecord check(MotorControllerRecord motorControllerRecord) throws SerialPortException, SerialPortTimeoutException, InterruptedException {
        // Looks to see what the values are of the different inputs in the motor controller
        boolean[] inputStatus = getInputStatus();
        boolean isTopSensorTriggered = inputStatus[MotorControllerConstants.TOP_SENSOR_INPUT_NUMBER-1];
        logger.debug("Top sensor status " + ((isTopSensorTriggered==true)?"on":"off"));
        boolean isMiddleSensorTriggered = inputStatus[MotorControllerConstants.MIDDLE_SENSOR_INPUT_NUMBER-1];
        logger.debug("Middle sensor status " + ((isMiddleSensorTriggered==true)?"on":"off"));
        boolean isBottomSensorTriggered = inputStatus[MotorControllerConstants.BOTTOM_SENSOR_INPUT_NUMBER-1];
        logger.debug("Bottom sensor status " + ((isBottomSensorTriggered==true)?"on":"off"));
        boolean isWarningZoneTriggered = inputStatus[MotorControllerConstants.SCANNER_WARNING_INPUT_NUMBER-1];
        logger.debug("Warning zone status " + ((isWarningZoneTriggered==true)?"active":"inactive"));

        motorControllerRecord.setTopLiftSensor(isTopSensorTriggered);
        motorControllerRecord.setMiddleLiftSensor(isMiddleSensorTriggered);
        motorControllerRecord.setBottomLiftSensor(isBottomSensorTriggered);
        motorControllerRecord.setWarningZone(isWarningZoneTriggered);

        return motorControllerRecord;
    }

    public boolean[] getInputStatus() throws SerialPortException, SerialPortTimeoutException, InterruptedException {
        int[] getInputStatus1 = createInputStatusCallAxis1();
        int[] getInputStatus2 = createInputStatusCallAxis2();
//        synchronized (MotorOutputCommand.class) {
            int[] inputResponse1 = motorControllerWrite.run(getInputStatus1,2);
            int[] inputResponse2 = motorControllerWrite.run(getInputStatus2,2);
            boolean[] inputStatus = filterInputResponse(inputResponse1, inputResponse2);
            return inputStatus;
//        }
    }

    public int[] createInputStatusCallAxis1() {
        int[] getInputStatus = new int[]{byteLengthInputQuery, MotorControllerConstants.AXIS_1_ID_HIGH, MotorControllerConstants.AXIS_1_ID_LOW, MotorControllerConstants.COMMAND_INPUT_CODE_HIGH,
                MotorControllerConstants.COMMAND_INPUT_CODE_LOW, MotorControllerConstants.INPUT_MESSAGE_QUERY_1_LOW, MotorControllerConstants.INPUT_MESSAGE_QUERY_1_HIGH, MotorControllerConstants.INPUT_MESSAGE_QUERY_2_LOW,
                MotorControllerConstants.INPUT_MESSAGE_QUERY_2_HIGH, 0};
        int checkSum = IOUtils.checkSum(getInputStatus);
        getInputStatus[byteLengthInputQuery+1] = checkSum;
        return getInputStatus;
    }

    public int[] createInputStatusCallAxis2() {
        int[] getInputStatus = new int[]{byteLengthInputQuery, MotorControllerConstants.AXIS_2_ID_HIGH, MotorControllerConstants.AXIS_2_ID_LOW, MotorControllerConstants.COMMAND_INPUT_CODE_HIGH,
                MotorControllerConstants.COMMAND_INPUT_CODE_LOW, MotorControllerConstants.INPUT_MESSAGE_QUERY_1_LOW, MotorControllerConstants.INPUT_MESSAGE_QUERY_1_HIGH, MotorControllerConstants.INPUT_MESSAGE_QUERY_2_LOW,
                MotorControllerConstants.INPUT_MESSAGE_QUERY_2_HIGH, 0};
        int checkSum = IOUtils.checkSum(getInputStatus);
        getInputStatus[byteLengthInputQuery+1] = checkSum;
        return getInputStatus;
    }


    public boolean[] filterInputResponse(int[] controllerResponse1, int[] controllerResponse2){
        int responseLength1 = controllerResponse1.length;
        int responseLength2 = controllerResponse2.length;
        int[] dataBytes1 = Arrays.copyOfRange(controllerResponse1, 6, responseLength1-1); //getting the part of the response that includes the input data
        int intData1 = ByteToolbox.byteArrayToInt(dataBytes1, 8);
        int[] dataBytes2 = Arrays.copyOfRange(controllerResponse2, 6, responseLength2-1); //from controller 2
        int intData2 = ByteToolbox.byteArrayToInt(dataBytes2, 8);
        boolean[] inputStatus = byteToBoolean(intData1, intData2);
        return inputStatus;
    }

    public boolean[] byteToBoolean(int convertByte1, int convertByte2){ //Only getting first 3 because only 3 inputs
        boolean input1 = (((convertByte1 & Constants.MASK_BIT_2) / Constants.MASK_BIT_2) == 1) ? true : false; //input 2/LSP = bit 13
        boolean input2 = (((convertByte1 & Constants.MASK_BIT_3) / Constants.MASK_BIT_3) == 1) ? true : false; //input 2/LSN = bit 14
        boolean input3 = (((convertByte2 & Constants.MASK_BIT_2) / Constants.MASK_BIT_2) == 1) ? true : false;
        boolean input4 = (((convertByte2 & Constants.MASK_BIT_3) / Constants.MASK_BIT_3) == 1) ? false : true; //safety signal is normally 24V
        logger.debug("Input 1-4 are: " + input1 + ", " + input2 + ", " + input3, ", " + input4);
        return new boolean[] {input1, input2, input3, input4};
    }
}
