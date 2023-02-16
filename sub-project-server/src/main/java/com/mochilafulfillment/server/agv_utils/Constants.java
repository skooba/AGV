package com.mochilafulfillment.server.agv_utils;

import org.hipparchus.linear.Array2DRowRealMatrix;

// Values shared across the system
public class Constants {
    // Internal loop pauses - make this number higher for debugging
    public static final int LOOP_PAUSE_TIME = 10;
    public static final int FAST_LOOP_PAUSE_TIME = 1;
    public static final int STOP_PAUSE_TIME = 500;
    public static final int TIME_STEP = 67; //approx 15Hz loop rate
    public static final long TERMINAL_READER_PAUSE_TIME = 20;
    public static final int PAUSE_BEFORE_END = 1000;

    // Serial ports
    public static final String CONTROLLER_PORT = "/dev/ttyUSBMOT"; //Need to configure AGV PC to static USB port
    public static final String PF_PORT = "/dev/ttyUSBSCAN2";
    public static final int POSITION_SCANNER_TIMEOUT = 20;
    public static final int MOTOR_CONTROLLER_TIMEOUT = 250;
    public static final int MOTOR_CONTROLLER_ERROR_COUNTS = 4;


    // Operation modes
    public static final String REMOTE_CONTROL_MODE = "Remote Control Mode";
    public static final String AUTOMATIC_MODE = "Automatic Mode";

    // Motion strings
    public static final String STOP_STRING = "STOPPED";
    public static final String TERMINATE_STRING = "TERMINATE";
    public static final String FORWARDS_STRING = "FORWARDS";
    public static final String BACKWARDS_STRING = "BACKWARDS";
    public static final String CW_STRING = "CLOCKWISE";
    public static final String CCW_STRING = "COUNTER_CLOCKWISE";
    public static final String MOTOR_POSITIVE = "";
    public static final String MOTOR_NEGATIVE = "-";

    //Safety Scanner Constants
    public static final int REGULAR_SCAN = 1;
    public static final int PICKING_SCAN = 2;

    // Lock Constants
    public static final int STOP_ON_TAG_KEY = 1;
    public static final int ROTATION_KEY = 2;
    public static final int FORK_LIFT_KEY = 3;

    //Lift constants
    public static final int LIFT_CONSTANT = 1;
    public static final int LOWER_CONSTANT = 2;
    public static final int STOP_VERTICAL_CONSTANT = 0;

    //Path record fork lift types
    public static final int PICK_LINE = 1;
    public static final int LIFT_LINE = 2;
    public static final int LOWER_LINE = 3;
    public static final int PLACE_LINE = 4;

    //Velocity and accelerations
    public static final int STANDARD_VELOCITY = 25;
    public static final int BACKWARDS_VELOCITY = 15;
    public static final int STANDARD_ACCELERATION = 12000;
    public static final int STOPPING_ACCELERATION = 12000;
    public static final int BACKWARDS_ACCELERATION = 12000;
    public static final int ONE_TAG_AWAY_VELOCITY = 10;
    public static final int ONE_TAG_AWAY_ACCEL = 12000;
    public static final int ON_STOP_TAG_VELOCITY = 2;
    public static final int ON_STOP_TAG_ACCEL = 12000;
    public static final int ROTATE_SPEED = 4;
    public static final int ROTATE_ACCEL = 12000;
    public static final int NUMBER_OF_LOOPS_BEFORE_CHANGE_VELOCITY = 8;
    public static final double MAX_VELOCITY_CHANGE = 5d;

    // Stop on Tag
    public static final int ZERO_X_COUNTS_CUTOFF = 1000;
    public static final int X_POSITION_STOP_OFFSET = 0; //Accounts for the time it takes for AGV to actually stop after finding center

    //Straight Line
    public static final int BACK_ON_TAPE_Y_POSITION_MINIMUM = 10;
    public static final int ZERO_Y_COUNTS_CUTOFF = 1000; //number of loops agv will do if off the line before stopping
    public static final int CONTROL_LOOP_TYPE = 2; //1-PID, 2-LQR
    public static final int PID_CONTROL = 1;
    public static final int LQR_CONTROL = 2;

    // Rotation
    public static final int ON_TARGET_ANGLE_COUNTER = 1;
    public static final int FINAL_ANGLE_TOLERANCE = 2;
    public static final int ROTATION_TYPE = 2; //1-NO CONTROL, 2- LQR
    public static final int NO_CONTROL_ROTATION = 1;
    public static final int LQR_ROTATION = 2;

    // Pid variables
    public static final double KP = 0.00075;
    public static final double KI = 0.00000000071;
    public static final double KD = 0.00000002;
    public static final int INTEGRAL_TYPE = 0; //0=standard, 1=truncates at max allowed, 2=truncates by time and scales
    public static final double MAX_INTEGRAL_ALLOWED = 1000;
    public static final double MIN_INTEGRAL_ALLOWED = -1000;
    public static final int KI_SECONDS = 12; //40% = 12 // in seconds how long the integral term accrues results
    public static final int INTEGRAL_WEIGHT_FACTOR = 5; //was 1 how many times bigger can the integral term be then the derivative term at its absolute max

    //LQR Constants
    public static final Array2DRowRealMatrix LQR_Q = new Array2DRowRealMatrix(
            new double[][] {
                    new double[] {2, 0, 0},
                    new double[] {0, 2, 0},
                    new double[] {0, 0, 4.2E4},
            }
    );
    public static final Array2DRowRealMatrix LQR_R = new Array2DRowRealMatrix(
            new double[][] {
                    new double[] {2.5E5, 0},
                    new double[] {0, 2.5E5},
            }
    );

    public static final Array2DRowRealMatrix LQR_Q_ROTATION = new Array2DRowRealMatrix(
            new double[][] {
                    new double[] {25, 0, 0},
                    new double[] {0, 75, 0},
                    new double[] {0, 0, 10}
            }
    );

    public static final Array2DRowRealMatrix LQR_R_ROTATION = new Array2DRowRealMatrix(
            new double[][] {
                    new double[] {.5, 0},
                    new double[] {0, .5}
            }
    );

    public static final  Array2DRowRealMatrix NEGATIVE_IDENTITY = new Array2DRowRealMatrix(
            new double[][] {
                    new double[] {-1}
            });
    public static final double EFFECTIVE_0 = 1E-20;

    //Mechanical Settings
    public static final double WHEEL_RADIUS = 4d; //inches
    public static final double WHEEL_DISTANCE = 32.5; //inches
    public static final double ENCODER_LINES = 5000d;
    public static final double GEAR_BOX_RATIO = 10d;
    public static final double MAX_SPEED_AGV = 60d; //in/s
    public static final int SCANNER_Y_OFFSET = 0; // in mm (follow coordinate system of tags)
    public static final int SCANNER_X_OFFSET = 0; // in mm (follow coordinate system of tags)

    //File info
    public static final String TEST_FILE_NAME = "testpath.csv";
    public static final String FILE_NAME = "pickpath.csv";

    //Binary Constants
    public static final int MASK_BIT_0 = 1;
    public static final int MASK_BIT_1 = 2;
    public static final int MASK_BIT_2 = 4;
    public static final int MASK_BIT_3 = 8;
    public static final int MASK_BIT_4 = 16;
    public static final int MASK_BIT_5 = 32;
    public static final int MASK_BIT_6 = 64;
    public static final int MASK_BIT_7 = 128;
    public static final int MASK_BIT_8 = 256;
    public static final int MASK_BIT_9 = 512;
    public static final int MASK_BIT_10 = 1024;
    public static final int MASK_BIT_11 = 2048;
    public static final int MASK_BIT_12 = 4096;
    public static final int MASK_BIT_13 = 8192;
    public static final int MASK_BIT_14 = 16384;
    public static final int MASK_BIT_15 = 32768;
}
