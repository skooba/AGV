package com.mochilafulfillment.server.position_scanner;

import com.mochilafulfillment.server.agv_utils.ByteToolbox;
import com.mochilafulfillment.server.agv_utils.Exceptions.PGVException;
import com.mochilafulfillment.server.position_scanner.dtos.PositionScannerResponseRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TagResponseTest {
    private static final int requestDirectionByte1 = 0;
    private static final int requestDirectionByte2 = 0;
    private static final int requestDirectionByte3 = 0;
    private static final int byte1 = 2;
    private static final int byte2 = 70;
    private static final int byte3 = 0;
    private static final int byte3_4 = 4;
    private static final int byte3_7 = 7;
    private static final int byte4 = 0;
    private static final int byte4_127 = 127;
    private static final int byte5 = 0;
    private static final int byte5_1000_Y = ByteToolbox.toByte((1000 * 10) >>> 7);
    private static final int byte5_1001_Y = ByteToolbox.toByte((1000 * 10) >>> 7);
    private static final int byte5_127 = 127;
    private static final int byte6 = 1;
    private static final int byte6_10 = 10;
    private static final int byte6_1000_Y = 1000 * 10 - (byte5_1000_Y << 7);
    private static final int byte6_1001_Y = 1001 * 10 - (byte5_1001_Y << 7);
    private static final int byte6_127 = 127;
    private static final int byte7 = 0;
    private static final int byte7_UNDER_MAX_Y_POS = ByteToolbox.toByte((PositionScannerConstants.MAX_Y_POSITION * 10 - 1) >>> 7);
    private static final int byte7_OVER_MAX_Y_POS = ByteToolbox.toByte((PositionScannerConstants.MAX_Y_POSITION * 10) >>> 7);
    private static final int byte7_OVER_MAX_Y_NEG = 127 - (ByteToolbox.toByte((PositionScannerConstants.MAX_Y_POSITION * 10 + 1) >>> 7));
    private static final int byte7_UNDER_MAX_Y_NEG = 127 - (ByteToolbox.toByte((PositionScannerConstants.MAX_Y_POSITION * 10 - 1) >>> 7));
    private static final int byte7_1000_Y = 127 - (ByteToolbox.toByte((1000 * 10 - 1) >>> 7));
    private static final int byte7_1001_Y = 127 - (ByteToolbox.toByte((1001 * 10 - 1) >>> 7));
    private static final int byte7_127 = 127;
    private static final int byte8 = 1;
    private static final int byte8_2 = 2;
    private static final int byte8_10 = 10;
    private static final int byte8_UNDER_MAX_Y_POS = (PositionScannerConstants.MAX_Y_POSITION * 10 - 1) - (byte7_OVER_MAX_Y_POS << 7) + 1;
    private static final int byte8_OVER_MAX_Y_POS = PositionScannerConstants.MAX_Y_POSITION * 10 - (byte7_OVER_MAX_Y_POS << 7) + 1;
    private static final int byte8_UNDER_MAX_Y_NEG = 128 - ((PositionScannerConstants.MAX_Y_POSITION * 10 - 1) - ((127 - byte7_OVER_MAX_Y_NEG) << 7));
    private static final int byte8_OVER_MAX_Y_NEG = 126 - ((PositionScannerConstants.MAX_Y_POSITION * 10 - 1) - ((127 - byte7_OVER_MAX_Y_NEG) << 7));
    private static final int byte8_1000_Y = 1000 * 10 - (byte7_1000_Y << 7);
    private static final int byte8_1001_Y = 1001 * 10 - (byte7_1001_Y << 7);
    private static final int byte8_127 = 127;
    private static final int byte9 = 0;
    private static final int byte10 = 0;
    private static final int byte11 = 0;
    private static final int byte11_45degs = ByteToolbox.toByte((45 * 10) >>> 7);
    private static final int byte11_90degs = ByteToolbox.toByte((90 * 10) >>> 7);
    private static final int byte11_180degs = ByteToolbox.toByte((180 * 10) >>> 7);
    private static final int byte11_270degs = ByteToolbox.toByte((270 * 10) >>> 7);
    private static final int byte11_360degs = ByteToolbox.toByte((360 * 10) >>> 7);
    private static final int byte11_361degs = ByteToolbox.toByte((361 * 10) >>> 7);
    private static final int byte11_1000degs = ByteToolbox.toByte((1000 * 10) >>> 7);
    private static final int byte11_1001degs = ByteToolbox.toByte((1001 * 10) >>> 7);
    private static final int byte12 = 0;
    private static final int byte12_45degs = 45 * 10 - (byte11_45degs << 7);
    private static final int byte12_90degs = 90 * 10 - (byte11_90degs << 7);
    private static final int byte12_180degs = 180 * 10 - (byte11_180degs << 7);
    private static final int byte12_270degs = 270 * 10 - (byte11_270degs << 7);
    private static final int byte12_360degs = 360 * 10 - (byte11_360degs << 7);
    private static final int byte12_361degs = 361 * 10 - (byte11_360degs << 7);
    private static final int byte12_1000degs = 1000 * 10 - (byte11_360degs << 7);
    private static final int byte12_1001degs = 1001 * 10 - (byte11_360degs << 7);
    private static final int byte13 = 0;
    private static final int byte14 = 0;
    private static final int byte15 = 0;
    private static final int byte15_1 = 1;
    private static final int byte15_127 = 127;
    private static final int byte16 = 0;
    private static final int byte16_1 = 1;
    private static final int byte16_127 = 127;
    private static final int byte17 = 0;
    private static final int byte17_1 = 1;
    private static final int byte17_40 = 40;
    private static final int byte17_127 = 127;
    private static final int byte18 = 0;
    private static final int byte18_40 = 40;
    private static final int byte18_1 = 1;
    private static final int byte18_127 = 127;
    private static final int byte19 = 0;
    private static final int byte20 = 0;
    private static final int byte21 = 0;

    TagResponse testClass;

    @BeforeEach
    public void init() {
        PositionScannerResponseRecord scannerRecord = new PositionScannerResponseRecord();
        testClass = new TagResponse(scannerRecord);
    }

    @Test
    public void yPositionPositiveTest() throws PGVException {
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8_10, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};

        double response = testClass.yPosition(testBytes);

        Assertions.assertEquals(response,1);

    }

    @Test
    public void yPositionPositiveTooBigTest(){
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7_OVER_MAX_Y_POS, byte8_OVER_MAX_Y_POS, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};

        Assertions.assertThrows(PGVException.class, ()->testClass.yPosition(testBytes));

    }

    @Test
    public void yPositionPositiveNotTooBigTest(){
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7_UNDER_MAX_Y_POS, byte8_UNDER_MAX_Y_POS, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};

        Assertions.assertDoesNotThrow(()->testClass.yPosition(testBytes));

    }

    @Test
    public void yPositionNegativeTest() throws PGVException {
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7_127, byte8_127, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};

        double response = testClass.yPosition(testBytes);

        Assertions.assertEquals(response,-.1);
    }

    @Test
    public void yPositionNegativeTooBigTest() {
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7_OVER_MAX_Y_NEG, byte8_OVER_MAX_Y_NEG, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};

        Assertions.assertThrows(PGVException.class, ()->testClass.yPosition(testBytes));
    }

    @Test
    public void yPositionNegativeNotTooBigTest() {
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7_UNDER_MAX_Y_NEG, byte8_UNDER_MAX_Y_NEG, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};

        Assertions.assertDoesNotThrow(()->testClass.yPosition(testBytes));
    }

    @Test
    public void angle0Test() throws PGVException {
        int[] testBytes = {byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};
        double response = testClass.angle(testBytes);
        Assertions.assertEquals(response,0);
    }

    @Test
    public void angle45Test() throws PGVException {
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8, byte9, byte10, byte11_45degs, byte12_45degs, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};
        double response = testClass.angle(testBytes);
        Assertions.assertEquals(response,45);
    }

    @Test
    public void angle90Test() throws PGVException {
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8, byte9, byte10, byte11_90degs, byte12_90degs, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};
        double response = testClass.angle(testBytes);
        Assertions.assertEquals(response,90);
    }

    @Test
    public void angle180Test() throws PGVException {
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8, byte9, byte10, byte11_180degs, byte12_180degs, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};
        double response = testClass.angle(testBytes);
        Assertions.assertEquals(response,180);
    }

    @Test
    public void angle270Test() throws PGVException {
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8, byte9, byte10, byte11_270degs, byte12_270degs, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};
        double response = testClass.angle(testBytes);
        Assertions.assertEquals(response,270);
    }

    @Test
    public void angle360Test() {
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8, byte9, byte10, byte11_360degs, byte12_360degs, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};
        Assertions.assertThrows(PGVException.class, ()->testClass.angle(testBytes));
    }

    @Test
    public void angleTooBigTest() throws PGVException {
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8, byte9, byte10, byte11_361degs, byte12_361degs, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};
        Assertions.assertThrows(PGVException.class, ()->testClass.angle(testBytes));
    }

    @Test
    public void tagID0ByteTest(){
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};
        int response = testClass.tagId(testBytes);
        Assertions.assertEquals(response,0);
    }

    @Test
    public void tagID1ByteTest(){
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18_1, byte19, byte20, byte21};
        int response = testClass.tagId(testBytes);
        Assertions.assertEquals(response,1);
    }

    @Test
    public void tagID2ByteTest(){
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17_1, byte18, byte19, byte20, byte21};
        int response = testClass.tagId(testBytes);
        Assertions.assertEquals(response,128);
    }

    @Test
    public void tagID3ByteTest(){
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16_1, byte17, byte18, byte19, byte20, byte21};
        int response = testClass.tagId(testBytes);
        Assertions.assertEquals(response,16384);
    }

    @Test
    public void tagID4ByteTest(){
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15_1, byte16, byte17, byte18, byte19, byte20, byte21};
        int response = testClass.tagId(testBytes);
        Assertions.assertEquals(response,2097152);
    }

    @Test
    public void tagIDMaxByteTest(){
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15_127, byte16_127, byte17_127, byte18_127, byte19, byte20, byte21};
        int response = testClass.tagId(testBytes);
        Assertions.assertEquals(response,268435455);
    }

    @Test
    public void xPositionPositiveTest(){
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6_10, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};
        int[] testBytes2 = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6_127, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};
        int[] testBytes3 = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5_127, byte6_127, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};
        double response = testClass.xPosition(testBytes);
        double response2 = testClass.xPosition(testBytes2);
        double response3 = testClass.xPosition(testBytes3);

        Assertions.assertEquals(response,1);
        Assertions.assertEquals(response2,12.7);
        Assertions.assertEquals(response3,1638.3);
    }

    @Test
    public void xPositionNegativeTest(){
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3_7, byte4_127, byte5_127, byte6_127, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};
        int[] testBytes2 = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3_7, byte4_127, byte5_127, byte6, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};
        int[] testBytes3 = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3_7, byte4, byte5, byte6, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};
        int[] testBytes4 = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3_4, byte4, byte5, byte6, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};


        double response = testClass.xPosition(testBytes);
        double response2 = testClass.xPosition(testBytes2);
        double response3 = testClass.xPosition(testBytes3);
        double response4 = testClass.xPosition(testBytes4);

        Assertions.assertEquals(response,-.1);
        Assertions.assertEquals(response2,-12.7);
        Assertions.assertEquals(response3,-209715.1);
        Assertions.assertEquals(response4, -838860.7);
    }

    @Test
    public void isGridRepresentationFalseTest(){
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3_7, byte4_127, byte5_127, byte6_127, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16_1, byte17, byte18, byte19, byte20, byte21};
        int[] testBytes2 = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3_7, byte4_127, byte5_127, byte6_127, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16_1, byte17_127, byte18_127, byte19, byte20, byte21};

        boolean response = testClass.isGridRepresentation(testBytes);
        boolean response2 = testClass.isGridRepresentation(testBytes2);

        Assertions.assertFalse(response);
        Assertions.assertFalse(response2);

    }

    @Test
    public void isGridRepresentationTrueTest(){
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3_7, byte4_127, byte5_127, byte6_127, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};
        int[] testBytes2 = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3_7, byte4_127, byte5_127, byte6_127, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17_127, byte18_127, byte19, byte20, byte21};

        boolean response = testClass.isGridRepresentation(testBytes);
        boolean response2 = testClass.isGridRepresentation(testBytes2);

        Assertions.assertTrue(response);
        Assertions.assertTrue(response2);
    }

    @Test
    public void gridColumns(){
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3_7, byte4_127, byte5_127, byte6_127, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18_127, byte19, byte20, byte21};
        int[] testBytes2 = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3_7, byte4_127, byte5_127, byte6_127, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17_127, byte18, byte19, byte20, byte21};
        int[] testBytes3 = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3_7, byte4_127, byte5_127, byte6_127, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18_40, byte19, byte20, byte21};

        int response = testClass.gridColumns(testBytes);
        int response2 = testClass.gridColumns(testBytes2);
        int response3 = testClass.gridColumns(testBytes3);

        Assertions.assertEquals(response,127);
        Assertions.assertEquals(response2,0);
        Assertions.assertEquals(response3,40);

    }

    @Test
    public void gridRows(){
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3_7, byte4_127, byte5_127, byte6_127, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17_127, byte18, byte19, byte20, byte21};
        int[] testBytes2 = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3_7, byte4_127, byte5_127, byte6_127, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18_127, byte19, byte20, byte21};
        int[] testBytes3 = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3_7, byte4_127, byte5_127, byte6_127, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17_40, byte18, byte19, byte20, byte21};

        int response = testClass.gridRows(testBytes);
        int response2 = testClass.gridRows(testBytes2);
        int response3 = testClass.gridRows(testBytes3);

        Assertions.assertEquals(response,127);
        Assertions.assertEquals(response2,0);
        Assertions.assertEquals(response3,40);
    }

    @Test
    public void approachTagsFromFrontTest(){
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8_2, byte9, byte10, byte11_180degs, byte12_180degs, byte13, byte14, byte15, byte16, byte17, byte18_1, byte19, byte20, byte21};
        testClass.process(testBytes);
        PositionScannerResponseRecord testRecord = testClass.getPositionScannerRecord();

        Assertions.assertEquals(testRecord.getXPosition(), .1);
        Assertions.assertEquals(testRecord.getYPosition(), .2);
        Assertions.assertEquals(testRecord.getTapeAngle(),0);


    }

    @Test
    public void approachTagsFromBackTest(){
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8_2, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18_1, byte19, byte20, byte21};
        testClass.process(testBytes);
        PositionScannerResponseRecord testRecord = testClass.getPositionScannerRecord();


        Assertions.assertEquals(testRecord.getXPosition(), -.1);
        Assertions.assertEquals(testRecord.getYPosition(), -.2);
        Assertions.assertEquals(testRecord.getTapeAngle(),0);
    }


    @Test
    public void approachTagsFromRightTest(){
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8_2, byte9, byte10, byte11_90degs, byte12_90degs, byte13, byte14, byte15, byte16, byte17, byte18_1, byte19, byte20, byte21};
        testClass.process(testBytes);
        PositionScannerResponseRecord testRecord = testClass.getPositionScannerRecord();

        Assertions.assertEquals(testRecord.getXPosition(), .2);
        Assertions.assertEquals(testRecord.getYPosition(), -.1);
        Assertions.assertEquals(testRecord.getTapeAngle(),0);
    }

    @Test
    public void approachTagsFromLeftTest(){
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8_2, byte9, byte10, byte11_270degs, byte12_270degs, byte13, byte14, byte15, byte16, byte17, byte18_1, byte19, byte20, byte21};
        testClass.process(testBytes);
        PositionScannerResponseRecord testRecord = testClass.getPositionScannerRecord();

        Assertions.assertEquals(testRecord.getXPosition(), -.2);
        Assertions.assertEquals(testRecord.getYPosition(), .1);
        Assertions.assertEquals(testRecord.getTapeAngle(),0);

    }

    @Test
    public void angleErrorTest(){
        PositionScannerResponseRecord mockedRecord = Mockito.mock(PositionScannerResponseRecord.class);
        testClass.setPositionScannerRecord(mockedRecord);
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3_7, byte4_127, byte5_127, byte6_127, byte7, byte8, byte9, byte10, byte11_1001degs, byte12_1001degs, byte13, byte14, byte15, byte16, byte17, byte18_127, byte19, byte20, byte21};
        testClass.process(testBytes);

        Mockito.verify(mockedRecord, Mockito.never()).setXPosition(Mockito.anyDouble());
        Mockito.verify(mockedRecord, Mockito.never()).setYPosition(Mockito.anyDouble());
        Mockito.verify(mockedRecord, Mockito.never()).setTagAngle(Mockito.anyDouble());
        Mockito.verify(mockedRecord, Mockito.never()).setTapeAngle(Mockito.anyDouble());


    }

    @Test
    public void yPositionErrorTest(){
        PositionScannerResponseRecord mockedRecord = Mockito.mock(PositionScannerResponseRecord.class);
        testClass.setPositionScannerRecord(mockedRecord);
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3_7, byte4_127, byte5_127, byte6_127, byte7_1001_Y, byte8_1001_Y, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17_127, byte18, byte19, byte20, byte21};
        testClass.process(testBytes);

        Mockito.verify(mockedRecord, Mockito.never()).setXPosition(Mockito.anyDouble());
        Mockito.verify(mockedRecord, Mockito.never()).setYPosition(Mockito.anyDouble());
        Mockito.verify(mockedRecord, Mockito.never()).setTagAngle(Mockito.anyDouble());
        Mockito.verify(mockedRecord, Mockito.never()).setTapeAngle(Mockito.anyDouble());
    }

    @Test
    public void xPositionErrorTest(){
        PositionScannerResponseRecord mockedRecord = Mockito.mock(PositionScannerResponseRecord.class);
        testClass.setPositionScannerRecord(mockedRecord);
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5_1001_Y, byte6_1001_Y, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17_127, byte18, byte19, byte20, byte21};
        testClass.process(testBytes);

        Mockito.verify(mockedRecord, Mockito.never()).setXPosition(Mockito.anyDouble());
        Mockito.verify(mockedRecord, Mockito.never()).setYPosition(Mockito.anyDouble());
        Mockito.verify(mockedRecord, Mockito.never()).setTagAngle(Mockito.anyDouble());
        Mockito.verify(mockedRecord, Mockito.never()).setTapeAngle(Mockito.anyDouble());
    }

    @Test
    public void angleNoPositionTest(){
        PositionScannerResponseRecord mockedRecord = Mockito.mock(PositionScannerResponseRecord.class);
        testClass.setPositionScannerRecord(mockedRecord);
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3_7, byte4_127, byte5_127, byte6_127, byte7, byte8, byte9, byte10, byte11_1000degs, byte12_1000degs, byte13, byte14, byte15, byte16, byte17, byte18_127, byte19, byte20, byte21};
        testClass.process(testBytes);

        Mockito.verify(mockedRecord, Mockito.never()).setXPosition(Mockito.anyDouble());
        Mockito.verify(mockedRecord, Mockito.never()).setYPosition(Mockito.anyDouble());
        Mockito.verify(mockedRecord, Mockito.never()).setTagAngle(Mockito.anyDouble());
        Mockito.verify(mockedRecord, Mockito.never()).setTapeAngle(Mockito.anyDouble());
    }

    @Test
    public void yPositionNoPositionTest(){
        PositionScannerResponseRecord mockedRecord = Mockito.mock(PositionScannerResponseRecord.class);
        testClass.setPositionScannerRecord(mockedRecord);
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3_7, byte4_127, byte5_127, byte6_127, byte7_1000_Y, byte8_1000_Y, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17_127, byte18, byte19, byte20, byte21};
        testClass.process(testBytes);

        Mockito.verify(mockedRecord, Mockito.never()).setXPosition(Mockito.anyDouble());
        Mockito.verify(mockedRecord, Mockito.never()).setYPosition(Mockito.anyDouble());
        Mockito.verify(mockedRecord, Mockito.never()).setTagAngle(Mockito.anyDouble());
        Mockito.verify(mockedRecord, Mockito.never()).setTapeAngle(Mockito.anyDouble());
    }

    @Test
    public void xPositionNoPositionTest(){
        PositionScannerResponseRecord mockedRecord = Mockito.mock(PositionScannerResponseRecord.class);
        testClass.setPositionScannerRecord(mockedRecord);
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5_1000_Y, byte6_1000_Y, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17_127, byte18, byte19, byte20, byte21};
        testClass.process(testBytes);

        Mockito.verify(mockedRecord, Mockito.never()).setXPosition(Mockito.anyDouble());
        Mockito.verify(mockedRecord, Mockito.never()).setYPosition(Mockito.anyDouble());
        Mockito.verify(mockedRecord, Mockito.never()).setTagAngle(Mockito.anyDouble());
        Mockito.verify(mockedRecord, Mockito.never()).setTapeAngle(Mockito.anyDouble());
    }
}

