package com.mochilafulfillment.server.position_scanner;

import com.mochilafulfillment.server.agv_utils.ByteToolbox;
import com.mochilafulfillment.server.agv_utils.Exceptions.PGVException;
import com.mochilafulfillment.server.position_scanner.dtos.PositionScannerResponseRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TapeResponseTest {
    private static final int requestDirectionByte1 = 0;
    private static final int requestDirectionByte2 = 0;
    private static final int requestDirectionByte3 = 0;
    private static final byte byte1 = 0;
    private static final byte byte2 = 18;
    private static final byte byte2_4 = 4;
    private static final byte byte3 = 0;
    private static final byte byte4 = 0;
    private static final byte byte5 = 0;
    private static final byte byte6 = 0;
    private static final byte byte7 = 0;
    private static final int byte7_UNDER_MAX_Y_POS = ByteToolbox.toByte((PositionScannerConstants.MAX_Y_POSITION * 10 - 1) >>> 7);
    private static final int byte7_OVER_MAX_Y_POS = ByteToolbox.toByte((PositionScannerConstants.MAX_Y_POSITION * 10) >>> 7);
    private static final int byte7_OVER_MAX_Y_NEG = 127 - (ByteToolbox.toByte((PositionScannerConstants.MAX_Y_POSITION * 10 + 1) >>> 7));
    private static final int byte7_UNDER_MAX_Y_NEG = 127 - (ByteToolbox.toByte((PositionScannerConstants.MAX_Y_POSITION * 10 - 1) >>> 7));
    private static final byte byte7_127 = 127;
    private static final byte byte8 = 0;
    private static final int byte8_UNDER_MAX_Y_POS = (PositionScannerConstants.MAX_Y_POSITION * 10 - 1) - (byte7_OVER_MAX_Y_POS << 7) + 1;
    private static final int byte8_OVER_MAX_Y_POS = PositionScannerConstants.MAX_Y_POSITION * 10 - (byte7_OVER_MAX_Y_POS << 7) + 1;
    private static final int byte8_UNDER_MAX_Y_NEG = 128 - ((PositionScannerConstants.MAX_Y_POSITION * 10 - 1) - ((127 - byte7_OVER_MAX_Y_NEG) << 7));
    private static final int byte8_OVER_MAX_Y_NEG = 126 - ((PositionScannerConstants.MAX_Y_POSITION * 10 - 1) - ((127 - byte7_OVER_MAX_Y_NEG) << 7));
    private static final byte byte8_10 = 10;
    private static final byte byte8_127 = 127;
    private static final byte byte9 = 0;
    private static final byte byte10 = 0;
    private static final byte byte11 = 0;
    private static final int byte11_45degs = ByteToolbox.toByte((45 * 10) >>> 7);
    private static final int byte11_90degs = ByteToolbox.toByte((90 * 10) >>> 7);
    private static final int byte11_180degs = ByteToolbox.toByte((180 * 10) >>> 7);
    private static final int byte11_270degs = ByteToolbox.toByte((270 * 10) >>> 7);
    private static final int byte11_360degs = ByteToolbox.toByte((360 * 10) >>> 7);
    private static final int byte11_361degs = ByteToolbox.toByte((361 * 10) >>> 7);
    private static final byte byte12 = 0;
    private static final int byte12_45degs = 45 * 10 - (byte11_45degs << 7);
    private static final int byte12_90degs = 90 * 10 - (byte11_90degs << 7);
    private static final int byte12_180degs = 180 * 10 - (byte11_180degs << 7);
    private static final int byte12_270degs = 270 * 10 - (byte11_270degs << 7);
    private static final int byte12_360degs = 360 * 10 - (byte11_360degs << 7);
    private static final int byte12_361degs = 361 * 10 - (byte11_360degs << 7);
    private static final byte byte13 = 0;
    private static final byte byte14 = 0;
    private static final byte byte15 = 0;
    private static final byte byte16 = 0;
    private static final byte byte17 = 0;
    private static final byte byte18 = 0;
    private static final byte byte19 = 0;
    private static final byte byte20 = 0;
    private static final byte byte21 = 0;

    TapeResponse testClass;

    @BeforeEach
    public void init() {
        PositionScannerResponseRecord positionScannerRecord = new PositionScannerResponseRecord();
        testClass = new TapeResponse(positionScannerRecord);
    }

    @Test
    public void processTest(){
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};
        PositionScannerResponseRecord mockedRecord = mock(PositionScannerResponseRecord.class);
        testClass.setPositionScannerRecord(mockedRecord);
        testClass.process(testBytes);

        verify(mockedRecord).setXPosition(0);
        verify(mockedRecord).setTagId(0);
        verify(mockedRecord).setTapeAngle(0);
        verify(mockedRecord).setYPosition(0);
        verify(mockedRecord).setOnTape(true);


    }

    @Test
    public void laneFound(){
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};
        boolean response = testClass.laneFound(testBytes);
        Assertions.assertTrue(response);
    }


    @Test
    public void laneNotFound(){
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2_4, byte3, byte4, byte5, byte6, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};

        boolean response = testClass.laneFound(testBytes);
        Assertions.assertFalse(response);
    }

    @Test
    public void yPositionPositiveTest() throws PGVException {
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8_10, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};

        double response = testClass.yPosition(testBytes);

        Assertions.assertEquals(response,1);

    }

    @Test
    public void yPositionNegativeTest() throws PGVException {
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7_127, byte8_127, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};

        double response = testClass.yPosition(testBytes);

        Assertions.assertEquals(response,-.1);
    }

    @Test
    public void yPositionPositiveTooBigTest() {
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7_OVER_MAX_Y_POS, byte8_OVER_MAX_Y_POS, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};

        Assertions.assertThrows(PGVException.class, ()->testClass.yPosition(testBytes));

    }

    @Test
    public void yPositionPositiveNotTooBigTest() {
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7_UNDER_MAX_Y_POS, byte8_UNDER_MAX_Y_POS, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};

        Assertions.assertDoesNotThrow(()->testClass.yPosition(testBytes));

    }

    @Test
    public void yPositionNegativeNotTooBigTest() {
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7_UNDER_MAX_Y_NEG, byte8_UNDER_MAX_Y_NEG, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};

        Assertions.assertDoesNotThrow(()->testClass.yPosition(testBytes));
    }

    @Test
    public void yPositionNegativeTooBigTest() {
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7_OVER_MAX_Y_NEG, byte8_OVER_MAX_Y_NEG, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};

        Assertions.assertThrows(PGVException.class, ()->testClass.yPosition(testBytes));
    }

    @Test
    public void angle0Test() throws PGVException {
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};
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
    public void angle360Test() throws PGVException {
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8, byte9, byte10, byte11_360degs, byte12_360degs, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};
        Assertions.assertThrows(PGVException.class, ()->testClass.angle(testBytes));

    }

    @Test
    public void angleTooBigTest() throws PGVException {
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8, byte9, byte10, byte11_361degs, byte12_361degs, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};
        Assertions.assertThrows(PGVException.class, ()->testClass.angle(testBytes));
    }
}
