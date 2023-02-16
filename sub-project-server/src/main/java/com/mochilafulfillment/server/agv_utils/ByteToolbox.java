package com.mochilafulfillment.server.agv_utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByteToolbox {
    private static final Logger logger = LoggerFactory.getLogger(ByteToolbox.class);

    public static byte toByte(int integer){
        if (integer > 255){
            throw new IllegalArgumentException("Error: Only 255 bits allowed in a byte");
        }
        if (integer < 128){
            return (byte) integer;
        }
        return (byte) (integer - 256);
    }

    public static boolean isBitInInt(int anInt, int position){ //Starts at position 0
        if(anInt < 0) {
            logger.error("Error: IllegalArgument, only positive integers allowed");}
        int mask = (int) Math.pow(2, position +1) - 1;
        int maskedInt = anInt & mask; //set everything to 0 to the left of byte of interest
        return maskedInt >>> position == 1; //logical shift (>>>) does not consider signedness
    }

    public static int byteArrayToInt(int[] byteArray, int bitsPer){
        if (bitsPer > 8){
            logger.error("Error: Cannot have more than 8 bits per byte");
        }
        int val;
        val = (byteArray[0] & ((int)Math.pow(2,bitsPer)-1));
        for (int i=1; i< byteArray.length;i++){
            val = val << bitsPer | (byteArray[i] & ((int)Math.pow(2,bitsPer)-1)); //shift previous val to the left to make room for next byte
        }
        return val;
    }

    public static int[] intToByteArray(long val, int bitsPer) {
        if (bitsPer > 8) {
            logger.error("Error: Cannot have more than 8 bits per byte");
        }
        int[] byteArray = new int[4];
        int mask = (int) Math.pow(2, bitsPer) - 1; //mask to hide all but first byte
        for (int aByte=byteArray.length-1, i = 0; aByte>=0; aByte--, i++) {
            byteArray[aByte] =  (int)((mask & val) >>> (bitsPer*i));
            mask = mask << bitsPer;
        }
        return byteArray;
    }

    //twosComplement to a signed int
    public static int signBits(int bitValue, int numberOfBits){
        boolean isNegative = isBitInInt(bitValue, numberOfBits - 1);
        int empiricalOffset = -(int)Math.pow(2, numberOfBits-1);
        int mask = (int)Math.pow(2, numberOfBits-1) - 1; //mask to make the first digit a 0

        if (isNegative) {
            return empiricalOffset + (mask & bitValue);
        } else {
            return bitValue;
        }
    }

    //return long because Java twos complement can return up to 2^32-1
    public static long twosComplement(int signedInt, int numberOfBits){
        if (signedInt >= 0){
            return signedInt;
        } else {
            //incorporates all 0s to the left of the first 1
            long mask = (long)Math.pow(2, numberOfBits) -1;
            long shifted = (-(long)signedInt) << numberOfBits;
            long inverted = ~shifted;
            long shiftedBack = (inverted >>> numberOfBits) & mask;
            return  shiftedBack + 1;
        }
    }
}
