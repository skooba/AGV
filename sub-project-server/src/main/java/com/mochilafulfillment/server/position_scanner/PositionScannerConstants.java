package com.mochilafulfillment.server.position_scanner;

import jssc.SerialPort;

public class PositionScannerConstants {
    public static final int BAUD_RATE = 230400;
    public static final int PARITY = SerialPort.PARITY_EVEN;

    //Position inquiry
    public static final int b0xC8 =  200; // 11001000
    public static final int b0x37 =  55; // 00110111


    //Left lane request
    public static final int b0xE8 =  232; //11101000
    public static final int b0x17 =  23; //00010111

    //Blue tape request
    public static final int b0xC4 = 196; // 11000100
    public static final int b0x3B = 59; // 00111011

    public static final int[] REQUEST_TELEGRAM = new int[]{b0xC8, b0x37};
    public static final int[] REQUEST_LEFT_LANE = new int[]{b0xE8, b0x17};

    public static final int REQUEST_DIRECTION_BYTE_LENGTH = 3;
    public static final int REQUEST_POSITION_BYTE_LENGTH = 21;

    //Physical position scanner properties
    public static final int ANGLE_OFFSET = 0;
    public static final int MAX_Y_POSITION = 80;


    //Position scanner data properties
    public static int BITS_PER_BYTE = 7;
    public static int GRID_REPRESENTATION_TAG_ID_BIT = 14;




}
