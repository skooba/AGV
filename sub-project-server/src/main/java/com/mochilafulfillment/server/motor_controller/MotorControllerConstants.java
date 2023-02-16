package com.mochilafulfillment.server.motor_controller;

import com.mochilafulfillment.server.agv_utils.Constants;
import jssc.SerialPort;

public class MotorControllerConstants {

    //Controller parameters
    public static final double SLOW_LOOP_SAMPLING_PERIOD = 0.001; // in seconds. go to (drive setup) -> advanced to access

    // Serial
    public static final int BAUD_RATE = SerialPort.BAUDRATE_115200;
    public static final int RESTART_BAUD_RATE = SerialPort.BAUDRATE_9600;
    public static final int PARITY = 0;

    public static final double VELOCITY_MULTIPLIER = (Constants.MAX_SPEED_AGV/(Constants.WHEEL_RADIUS * 100d))/(((2d*3.1415d) / (4d * Constants.ENCODER_LINES * SLOW_LOOP_SAMPLING_PERIOD * Constants.GEAR_BOX_RATIO))); // In EasyMotion see Help -> Help Topics -> Application Programming -> Internal Units and Scaling Factor
    public static final double ACCEL_MULTIPLIER = VELOCITY_MULTIPLIER;
    public static final int[] SET_CONTROLLER_BAUD_TO_115200 = new int[] {0x06,0x10,0x00,0x08,0x20,0x00,0x04,0x42};



    // Responses
    public static final int EXPECTED_RESPONSE_FROM_COMMAND = 0x4F;
    public static final int SECOND_BYTE_EXPECTED_RESPONSE_FROM_COMMAND = 0x0A;
    public static final int MOTOR_CONTROLLER_INITIAL_REQUEST = 0xFF;
    public static final int MOTOR_CONTROLLER_INITIAL_RESPONSE = 0x0D;


    //Input Checks
    public static final int SCANNER_WARNING_INPUT_NUMBER = 4;
    public static final int TOP_SENSOR_INPUT_NUMBER = 3;
    public static final int MIDDLE_SENSOR_INPUT_NUMBER = 2;
    public static final int BOTTOM_SENSOR_INPUT_NUMBER = 1;

    // AxisID
    public static final int AXIS_1_ID_HIGH = 0x00;
    public static final int AXIS_1_ID_LOW = 0x10;
    public static final int AXIS_2_ID_HIGH = 0x00;
    public static final int AXIS_2_ID_LOW = 0x20;


    // OperationCode
    public static final int COMMAND_VELOCITY_CODE_HIGH = 0x24;
    public static final int COMMAND_VELOCITY_CODE_LOW = 0xA0;
    public static final int COMMAND_ACCELERATION_CODE_HIGH =0x24;
    public static final int COMMAND_ACCELERATION_CODE_LOW =0xA2;
    public static final int COMMAND_INPUT_CODE_HIGH = 0xB0;
    public static final int COMMAND_INPUT_CODE_LOW = 0x04;
    public static final int COMMAND_OUTPUT_CODE_HIGH = 0xEC;
    public static final int COMMAND_OUTPUT_CODE_LOW = 0x00;

    //Data int constants
    public static final int INPUT_MESSAGE_QUERY_1_LOW = 0x00;
    public static final int INPUT_MESSAGE_QUERY_1_HIGH = 0x11;
    public static final int INPUT_MESSAGE_QUERY_2_LOW = 0x09;
    public static final int INPUT_MESSAGE_QUERY_2_HIGH = 0x08;
    public static final int OUTPUT_2_MESSAGE_HIGH = 0x00;
    public static final int OUTPUT_2_MESSAGE_LOW = 0x04;
    public static final int OUTPUT_3_MESSAGE_HIGH = 0x00;
    public static final int OUTPUT_3_MESSAGE_LOW = 0x08;
    public static final int OUTPUT_OFF_MESSAGE_HIGH = 0x00;
    public static final int OUTPUT_OFF_MESSAGE_LOW = 0x00;

    //Full Commands
    public static final int[] UPDATE_GROUP_1 = new int[]{0x04, 0x10, 0x10, 0x01, 0x08, 0x2D};
    public static final int[] MODE_SP_AXIS_1 = new int[]{0x08, 0x00, 0x10, 0x59, 0x09, 0xBB, 0xC1, 0x83, 0x01, 0x7A}; //Trapezoidal speed mode
    public static final int[] MODE_SP_AXIS_2 = new int[]{0x08, 0x00, 0x20, 0x59, 0x09, 0xBB, 0xC1, 0x83, 0x01, 0x8A};
    public static final int[] AXIS_1_ON = new int[]{0x04, 0x00, 0x10, 0x01, 0x02, 0x17};
    public static final int[] AXIS_2_ON = new int[]{0x04, 0x00, 0x20, 0x01, 0x02, 0x27};


    // Exception handling
    public static final int NUMBER_OF_STOP_MOTOR_TRIES = 5;
    public static final int INITIALIZATION_TRIES = 15;
}
