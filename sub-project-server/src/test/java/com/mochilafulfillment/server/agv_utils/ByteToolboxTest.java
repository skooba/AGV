package com.mochilafulfillment.server.agv_utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ByteToolboxTest {

    @Test
    public void toByteTest() {
        Assertions.assertEquals(ByteToolbox.toByte(255), -1);
        Assertions.assertEquals(ByteToolbox.toByte(128), -128);
        Assertions.assertEquals(ByteToolbox.toByte(127), 127);
        Assertions.assertEquals(ByteToolbox.toByte(1), 1);
        Assertions.assertEquals(ByteToolbox.toByte(0), 0);
        Assertions.assertThrows(IllegalArgumentException.class, ()->ByteToolbox.toByte(256));
    }

    @Test
    public void isBitInInt(){
        Assertions.assertTrue(ByteToolbox.isBitInInt(255,0));
        Assertions.assertTrue(ByteToolbox.isBitInInt(255,1));
        Assertions.assertTrue(ByteToolbox.isBitInInt(255,2));
        Assertions.assertTrue(ByteToolbox.isBitInInt(255,3));
        Assertions.assertTrue(ByteToolbox.isBitInInt(255,4));
        Assertions.assertTrue(ByteToolbox.isBitInInt(255,5));
        Assertions.assertTrue(ByteToolbox.isBitInInt(255,6));
        Assertions.assertTrue(ByteToolbox.isBitInInt(255,7));
        Assertions.assertFalse(ByteToolbox.isBitInInt(255,8));
        Assertions.assertFalse(ByteToolbox.isBitInInt(254,0));
        Assertions.assertFalse(ByteToolbox.isBitInInt(256,0));
        Assertions.assertFalse(ByteToolbox.isBitInInt(256,1));
        Assertions.assertFalse(ByteToolbox.isBitInInt(256,2));
        Assertions.assertFalse(ByteToolbox.isBitInInt(256,3));
        Assertions.assertFalse(ByteToolbox.isBitInInt(256,4));
        Assertions.assertFalse(ByteToolbox.isBitInInt(256,5));
        Assertions.assertFalse(ByteToolbox.isBitInInt(256,6));
        Assertions.assertFalse(ByteToolbox.isBitInInt(256,7));
        Assertions.assertTrue(ByteToolbox.isBitInInt(256,8));

    }

    @Test
    public void byteArrayToIntTest(){
        Assertions.assertEquals(ByteToolbox.byteArrayToInt(new int[] {255,255,255}, 8), 16777215);
        Assertions.assertEquals(ByteToolbox.byteArrayToInt(new int[] {1,1}, 8), 257);
        Assertions.assertEquals(ByteToolbox.byteArrayToInt(new int[] {1,0,1}, 8), 65537);
        Assertions.assertEquals(ByteToolbox.byteArrayToInt(new int[] {1,52}, 8), 308);
        Assertions.assertEquals(ByteToolbox.byteArrayToInt(new int[] {1,52}, 7), 180);
    }

    @Test
    public void signBitsTest(){
        Assertions.assertEquals(ByteToolbox.signBits(127, 8), 127);
        Assertions.assertEquals(ByteToolbox.signBits(255, 8), -1);
        Assertions.assertEquals(ByteToolbox.signBits(253, 8), -3);
        Assertions.assertEquals(ByteToolbox.signBits(200, 8), -56);
        Assertions.assertEquals(ByteToolbox.signBits(127, 21), 127);
        Assertions.assertEquals(ByteToolbox.signBits(2000000, 21), -97152);
    }

    @Test
    public void intToByteArrayTest(){
        Assertions.assertArrayEquals(ByteToolbox.intToByteArray(16777215, 8), new int[] {0,255,255,255});
        Assertions.assertArrayEquals(ByteToolbox.intToByteArray(257, 8), new int[] {0,0,1,1});
        Assertions.assertArrayEquals(ByteToolbox.intToByteArray(65537, 8), new int[] {0,1,0,1});
        Assertions.assertArrayEquals(ByteToolbox.intToByteArray(308, 8), new int[] {0,0,1,52});
        Assertions.assertArrayEquals(ByteToolbox.intToByteArray(180, 7), new int[] {0,0,1,52});
    }

    @Test
    public void twosComplementTest(){
        Assertions.assertEquals(ByteToolbox.twosComplement(0,8),0);
        Assertions.assertEquals(ByteToolbox.twosComplement(1,8),1);
        Assertions.assertEquals(ByteToolbox.twosComplement(-1,8),255);
        Assertions.assertEquals(ByteToolbox.twosComplement(127,8),127);
        Assertions.assertEquals(ByteToolbox.twosComplement(1,16),1);
        Assertions.assertEquals(ByteToolbox.twosComplement(11,16),11);
        Assertions.assertEquals(ByteToolbox.twosComplement(-1,16),65535);
        Assertions.assertEquals(ByteToolbox.twosComplement(-11,16),65525);
        Assertions.assertEquals(ByteToolbox.twosComplement(32767,16),32767);
        Assertions.assertEquals(ByteToolbox.twosComplement(-32768,16),32768);
        Assertions.assertEquals(ByteToolbox.twosComplement(-65536,32),4294901760l);
    }
}
