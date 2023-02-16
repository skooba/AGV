package com.mochilafulfillment.server.controls_logic.dtos;

import com.mochilafulfillment.server.agv_utils.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PathRecordTest {
    @Test
    public void pathRecordConstructorTest(){
        PathRecord testPathRecord1 = new PathRecord(1,90, Constants.PICK_LINE);
        PathRecord testPathRecord2 = new PathRecord(2,180, Constants.LIFT_LINE);
        PathRecord testPathRecord3 = new PathRecord(100000,-90, Constants.LOWER_LINE);

        Assertions.assertEquals(testPathRecord1.getTagId(), 1);
        Assertions.assertEquals(testPathRecord1.getRotateToDegrees(), 90);
        Assertions.assertEquals(testPathRecord1.isPickLine(), true);
        Assertions.assertEquals(testPathRecord1.isLiftLine(), false);
        Assertions.assertEquals(testPathRecord1.isLowerLine(), false);

        Assertions.assertEquals(testPathRecord2.getTagId(), 2);
        Assertions.assertEquals(testPathRecord2.getRotateToDegrees(), 180);
        Assertions.assertEquals(testPathRecord2.isPickLine(), false);
        Assertions.assertEquals(testPathRecord2.isLiftLine(), true);
        Assertions.assertEquals(testPathRecord2.isLowerLine(), false);

        Assertions.assertEquals(testPathRecord3.getTagId(), 100000);
        Assertions.assertEquals(testPathRecord3.getRotateToDegrees(), -90);
        Assertions.assertEquals(testPathRecord3.isPickLine(), false);
        Assertions.assertEquals(testPathRecord3.isLiftLine(), false);
        Assertions.assertEquals(testPathRecord3.isLowerLine(), true);
    }
}
