package com.mochilafulfillment.server.api.laptop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mochilafulfillment.server.agv_utils.Constants.*;


public class LaptopGetOperationMode {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public String run(TerminalReader terminalReader) throws InterruptedException {
        logger.info("Press 1. to enter \"Remote Control Mode\" " +
                "\n OR" +
                "\n Press 2. to enter \"Automatic Mode\"");

        while (true) {
            String scan = terminalReader.getCurrentValue();
            logger.debug("Get Operations mode read " + scan);

            switch (scan) {
                case "1":
                    terminalReader.setCurrentValue("none");
                    logger.info("Remote Control Mode Selected \n" +
                            "Press the \"UP\" and \"DOWN\" arrow keys to change AGV move direction to forwards or backwards \n" +
                            "Press the \"RIGHT\" and \"LEFT\" arrow keys to rotate the AGV \n" +
                            "Press \"u\" or \"d\" to move the Fork Lift up or down \n" +
                            "Enter value between 0-100 into velocity text box to set the percentage of maximum wheel speed \n" +
                            "Enter value between 0-100 into acceleration text box to set the percentage of maximum wheel speed \n" +
                            "Press \"s\" to change the speed and acceleration to standard values \n" +
                            "Press \"2\" to enter automatic mode \n");
                    return REMOTE_CONTROL_MODE;
                case "2":
                    terminalReader.setCurrentValue("none");
                    logger.info("Automatic Mode Selected \n" +
                            "Press \"c\" to stop the motors and quit the program \n" +
                            "Press \"1\" to enter remote mode");
                    return AUTOMATIC_MODE;
                case "TERMINATE":
                    logger.error("Error: Terminate string was entered before either mode started");
                    return TERMINATE_STRING;
                case "none":
                    Thread.sleep(LOOP_PAUSE_TIME);
                    break;
                default:
                    if(scan == "thIs Is for a test9"){
                        throw new IllegalArgumentException();
                    }
                    logger.error("Error: Must enter either a 1 or a 2 to pick a mode");
                    Thread.sleep(LOOP_PAUSE_TIME);
            }


        }
    }


}
