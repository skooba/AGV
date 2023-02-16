package com.mochilafulfillment.server.api.laptop;

import java.util.Map;
import java.util.HashMap;
import java.util.Scanner; //Console input

import com.mochilafulfillment.server.agv_utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.logstash.logback.argument.StructuredArguments.kv;


// Process scanner inputs into descriptive strings
public class TerminalReader implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Linking keys to strings for easier debugging
    public static Map<String,String> keyMap = new HashMap<>();

    private String currentValue = "none";
    private String status = "on";
    private boolean exitAutoMode;
    private boolean exitRemoteMode;
    private boolean endProgram;

    public void run(){

        Scanner scanner = new Scanner(System.in);

        keyMap.put("c", Constants.TERMINATE_STRING);

        while (getStatus() == "on"){
            while(!scanner.hasNextLine()){
                try {
                    Thread.sleep(Constants.LOOP_PAUSE_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String scan = scanner.nextLine();

            try {
                if (keyMap.containsKey(scan)) {
                    currentValue = keyMap.get(scan);
                    logger.debug("currentValue is " + currentValue);
                } else if (isNumeric(scan)) {
                    int numericScan = Integer.parseInt(scan);
                    if (numericScan == 1) {
                        currentValue = scan;
                        logger.debug("Remote control mode (value = 1) entered into terminal reader");
                        setExitAutoMode(true);
                    } else if (numericScan == 2) {
                        currentValue = scan;
                        logger.debug("Automatic mode (value = 2) entered into terminal reader");
                        setExitRemoteMode(true);
                    } else {
                        throw new IllegalArgumentException("Valid input required in terminal reader");
                    }
                } else {
                    throw new IllegalArgumentException("Valid input required in terminal reader");
                }
            } catch(IllegalArgumentException e) {
                logger.error("Error: Please enter a valid input into terminal reader");
            }
            if (currentValue == Constants.TERMINATE_STRING) {
                logger.info("Terminate value entered into terminal reader");
                setEndProgram(true);
                setStatus("off");
            }
            try {
                Thread.sleep(Constants.TERMINAL_READER_PAUSE_TIME); // Wait between each line read
            } catch (Exception e) {
                if(e instanceof InterruptedException) {
                    throw new RuntimeException("wrapped InterruptedException", e);
                } else if (! (e instanceof IllegalArgumentException)){
                    throw new RuntimeException("Did not expect this type of exception", e);
                }
            }
        }
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(String currentValue){
        this.currentValue = currentValue;
    }

    public static boolean isNumeric(String str){
        Double.parseDouble(str);
        return true;
    }

    public void setExitRemoteMode(boolean exitRemoteMode) {
        this.exitRemoteMode = exitRemoteMode;
    }

    public boolean isExitRemoteMode() {
        return exitRemoteMode;
    }

    public void setExitAutoMode(boolean exitAutoMode) {
        this.exitAutoMode = exitAutoMode;
    }

    public boolean isExitAutoMode() {
        return exitAutoMode;
    }

    public boolean isEndProgram() {
        return endProgram;
    }

    public void setEndProgram(boolean endProgram) {
        this.endProgram = endProgram;
    }


    public String getStatus() {return this.status;}

    public void setStatus(String status) {
        this.status = status;
    }

    public void setKeyMap(Map<String,String> keyMap){
        this.keyMap = keyMap;
    }

}
