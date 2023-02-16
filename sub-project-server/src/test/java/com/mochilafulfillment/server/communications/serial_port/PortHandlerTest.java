package com.mochilafulfillment.server.communications.serial_port;

import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.motor_controller.MotorControllerConstants;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

public class PortHandlerTest {

    SerialPort mockedPort;
    PortHandler portHandler;

    @BeforeEach
    public void init() {
        mockedPort = mock(SerialPort.class);
        portHandler = new PortHandler(mockedPort);
    }

    @Test
    public void runPortTest() throws SerialPortException{
        portHandler.run(MotorControllerConstants.BAUD_RATE, MotorControllerConstants.PARITY);

        verify(mockedPort, times(1)).openPort();
        verify(mockedPort, times(1)).setParams(MotorControllerConstants.BAUD_RATE,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                MotorControllerConstants.PARITY);
    }

    @Test
    public void readSerialPortBytesExceptionTest() throws SerialPortException, SerialPortTimeoutException {
        when(mockedPort.getInputBufferBytesCount()).thenReturn(5);
        when(mockedPort.readIntArray(5, Constants.POSITION_SCANNER_TIMEOUT)).thenReturn(new int[] {0x01,0x02,0x03});
        Assertions.assertThrows(SerialPortException.class,()->portHandler.readSerialPortBytes(5,true, Constants.POSITION_SCANNER_TIMEOUT));
        verify(mockedPort).readBytes();
    }

    @Test
    public void readSerialPortBytesPassTest() throws SerialPortException, SerialPortTimeoutException {
        when(mockedPort.getInputBufferBytesCount()).thenReturn(0);
        when(mockedPort.readIntArray(10, Constants.POSITION_SCANNER_TIMEOUT)).thenReturn(new int[] {0x01,0x02,0x03});
        int[] responseBytes = portHandler.readSerialPortBytes(10, true, Constants.POSITION_SCANNER_TIMEOUT);
        Assertions.assertArrayEquals(responseBytes, new int[] {0x01,0x02,0x03});
    }
}
