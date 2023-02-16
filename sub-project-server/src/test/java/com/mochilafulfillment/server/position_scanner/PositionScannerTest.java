package com.mochilafulfillment.server.position_scanner;

import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.position_scanner.dtos.PositionScannerResponseRecord;
import com.mochilafulfillment.server.communications.serial_port.PortHandler;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class PositionScannerTest {
    private static final int requestDirectionByte1 = 0;
    private static final int requestDirectionByte2 = 0;
    private static final int requestDirectionByte3 = 0;
    private static final int byte1 = 2;
    private static final int byte2 = 70;
    private static final int byte2_3 = 3;
    private static final int byte3 = 0;
    private static final int byte4 = 0;
    private static final int byte5 = 0;
    private static final int byte6 = 1;
    private static final int byte7 = 0;
    private static final int byte8 = 1;
    private static final int byte9 = 0;
    private static final int byte10 = 0;
    private static final int byte11 = 0;
    private static final int byte12 = 0;
    private static final int byte13 = 0;
    private static final int byte14 = 0;
    private static final int byte15 = 0;
    private static final int byte16 = 0;
    private static final int byte17 = 0;
    private static final int byte18 = 0;
    private static final int byte19 = 0;
    private static final int byte20 = 0;
    private static final int byte21 = 0;

    PositionScanner testClass;

    @BeforeEach
    public void init(){
        PositionScannerResponseRecord positionScannerRecord = new PositionScannerResponseRecord();
        testClass = new PositionScanner("test port", positionScannerRecord);
    }

    @Test
    public void isOnTagTest(){
        Assertions.assertEquals(true, testClass.isOnTag(byte2));
        Assertions.assertEquals(false, testClass.isOnTag(byte2_3));
    }

    @Test
    public void processTagResponseTest(){
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};
        String response = testClass.processResponse(testBytes);
        Assertions.assertEquals(response, "Tag mode");
    }

    @Test
    public void processTapeResponseTest(){
        int[] testBytes = {requestDirectionByte1, requestDirectionByte2, requestDirectionByte3, byte1, byte2_3, byte3, byte4, byte5, byte6, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14, byte15, byte16, byte17, byte18, byte19, byte20, byte21};
        String response = testClass.processResponse(testBytes);
        Assertions.assertEquals(response, "Tape mode");
    }

    @Test
    public void receiveResponsePassTest() throws SerialPortException, SerialPortTimeoutException {
        SerialPort mockedPort = mock(SerialPort.class);
        when(mockedPort.getInputBufferBytesCount()).thenReturn(0);
        when(mockedPort.readIntArray(PositionScannerConstants.REQUEST_DIRECTION_BYTE_LENGTH + PositionScannerConstants.REQUEST_POSITION_BYTE_LENGTH, Constants.POSITION_SCANNER_TIMEOUT)).thenReturn(new int[] {122, 127, 0, 0, 50});
        PortHandler portHandler = new PortHandler(mockedPort);
        testClass.setPortHandler(portHandler);
        int[] response = testClass.receiveResponse();
        int[] compareResponse = new int[] {122, 127, 0, 0, 50, 66};
        Assertions.assertEquals(response[0], compareResponse[0]);
        Assertions.assertEquals(response[1], compareResponse[1]);
        Assertions.assertEquals(response[2], compareResponse[2]);
        Assertions.assertEquals(response[3], compareResponse[3]);
        Assertions.assertEquals(response[4], compareResponse[4]);
    }

    @Test
    public void receiveResponseTimeOutTest() throws SerialPortException, SerialPortTimeoutException {
        SerialPort mockedPort = mock(SerialPort.class);
        when(mockedPort.getInputBufferBytesCount()).thenReturn(0);
        when(mockedPort.readIntArray(PositionScannerConstants.REQUEST_DIRECTION_BYTE_LENGTH + PositionScannerConstants.REQUEST_POSITION_BYTE_LENGTH, Constants.POSITION_SCANNER_TIMEOUT+50)).thenThrow(new SerialPortTimeoutException("Test Port", "Test Method", Constants.POSITION_SCANNER_TIMEOUT+50));
        PortHandler portHandler = new PortHandler(mockedPort);
        PortHandler mockedPortHandler = mock(PortHandler.class);
        when(mockedPortHandler.readSerialPortBytes(5,true,Constants.POSITION_SCANNER_TIMEOUT)).thenReturn(new int[] {122, 127, 0, 0, 50});
        testClass.setPortHandler(portHandler);
        int[] response = testClass.receiveResponse();
        Assertions.assertNull(response);
    }

    @Test
    public void receiveResponseFaultySerialExceptionTest() {
        int[] response = testClass.receiveResponse();
        Assertions.assertNull(response);
    }

    @Test
    public void makeRequestTest() throws SerialPortException {
        PortHandler mockedPortHandler = mock(PortHandler.class);
        doReturn(true).when(mockedPortHandler).writeSerialPortBytes(any());
        testClass.setPortHandler(mockedPortHandler);
        testClass.makeRequest();

        Mockito.verify(mockedPortHandler).writeSerialPortBytes(PositionScannerConstants.REQUEST_LEFT_LANE);
        Mockito.verify(mockedPortHandler).writeSerialPortBytes(PositionScannerConstants.REQUEST_TELEGRAM);
    }

    @Test
    public void makeRequestExceptionTest() throws SerialPortException {
        PortHandler mockedPortHandler = mock(PortHandler.class);
        doReturn(false).when(mockedPortHandler).writeSerialPortBytes(any());

        SerialPort mockedPort = mock(SerialPort.class);
        doReturn("Test Port").when(mockedPort).getPortName();

        doReturn(mockedPort).when(mockedPortHandler).getSerialPort();
        testClass.setPortHandler(mockedPortHandler);

        Assertions.assertThrows(SerialPortException.class, ()->{
            testClass.makeRequest();
        });
    }

    @Test
    public void validResponseFromPositionScannerRunTest() throws InterruptedException, SerialPortException, SerialPortTimeoutException {
        int[] responseTelegram = new int[] {5, 6, 90};
        PortHandler mockedPortHandler = mock(PortHandler.class);
        testClass.setPortHandler(mockedPortHandler);
        PositionScanner mockedTestClass = Mockito.spy(testClass);
        doNothing().when(mockedTestClass).makeRequest();
        when(mockedTestClass.receiveResponse()).thenReturn(responseTelegram);
        Mockito.doReturn("test").when(mockedTestClass).processResponse(responseTelegram);
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(mockedTestClass);
        executor.awaitTermination(100, TimeUnit.MILLISECONDS);
        Mockito.verify(mockedTestClass, times(1)).receiveResponse();
        Mockito.verify(mockedTestClass).processResponse(responseTelegram);
        Assertions.assertTrue(mockedTestClass.getPositionScannerResponseRecord().isNewPositionScannerRecord());
    }

    @Test
    public void notReadyForNewPositionScannerRecordRunTest() throws SerialPortException, SerialPortTimeoutException {
        int[] responseTelegram = new int[] {5, 6, 90};
        PortHandler mockedPortHandler = mock(PortHandler.class);
        testClass.setPortHandler(mockedPortHandler);
        PositionScanner mockedTestClass = Mockito.spy(testClass);
        mockedTestClass.positionScannerRecord.setNewPositionScannerRecord(true);
        when(mockedTestClass.receiveResponse()).thenReturn(responseTelegram);
        Mockito.doReturn("test").when(mockedTestClass).processResponse(responseTelegram);
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(mockedTestClass);

        Mockito.verify(mockedPortHandler, never()).writeSerialPortBytes(PositionScannerConstants.REQUEST_LEFT_LANE);
        Mockito.verify(mockedPortHandler, never()).writeSerialPortBytes(PositionScannerConstants.REQUEST_TELEGRAM);
    }
}
