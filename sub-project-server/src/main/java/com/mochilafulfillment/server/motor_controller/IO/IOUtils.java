package com.mochilafulfillment.server.motor_controller.IO;

public class IOUtils {
    public static int checkSum(int[] byteArray){
        int checkSum = 0;
        for(int byteValue : byteArray){
            checkSum += byteValue;
        }
        checkSum %= 256;
        return checkSum;
    }

}
