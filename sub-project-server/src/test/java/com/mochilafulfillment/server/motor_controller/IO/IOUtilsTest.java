package com.mochilafulfillment.server.motor_controller.IO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IOUtilsTest {
    @Test
    public void checkSumTest() {
        Assertions.assertEquals(IOUtils.checkSum(new int[] {0x00, 0x00, 0x00}),0);
        Assertions.assertEquals(IOUtils.checkSum(new int[] {0xFF, 0xFF, 0x00}),254);
        Assertions.assertEquals(IOUtils.checkSum(new int[] {0xAB, 0x06, 0x10}),193);
        Assertions.assertEquals(IOUtils.checkSum(new int[] {0xFF, 0xAB, 0x93}),61);
    }
}
