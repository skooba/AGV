package com.mochilafulfillment.server.motor_controller;

import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.communications.serial_port.IncorrectResponseFromSerialPortException;
import com.mochilafulfillment.server.communications.serial_port.PortHandler;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class MotorOutputCommandTest {

    @Test
    public void getFilteredResponseTest() throws SerialPortTimeoutException, SerialPortException {
        int[] compareResponse = new int[] {8, 0, 32, 36, 162, 0, 0, 255, 255, 143};

        PortHandler mockedPortHandler = mock(PortHandler.class);
        when(mockedPortHandler.readSerialPortBytes(1, false, Constants.MOTOR_CONTROLLER_TIMEOUT)).thenReturn(new int[]{8});
        when(mockedPortHandler.readSerialPortBytes(9,true, Constants.MOTOR_CONTROLLER_TIMEOUT)).thenReturn(new int[]{0, 32, 36, 162, 0, 0, 255, 255, 143});
        MotorOutputCommand testClass = new MotorOutputCommand(mockedPortHandler);

        int[] testResponse = testClass.getFilteredResponse();

        Assertions.assertArrayEquals(testResponse, compareResponse);
    }

    @Test
    public void runMessageType1Test() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        int[] compareResponse = new int[] {79};
        int[] sendTest = new int[] {8, 0, 32, 36, 162, 0, 0, 255, 255, 143};

        PortHandler mockedPortHandler = mock(PortHandler.class);
        when(mockedPortHandler.writeSerialPortBytes(sendTest)).thenReturn(true);
        when(mockedPortHandler.readSerialPortBytes(1, false, Constants.MOTOR_CONTROLLER_TIMEOUT)).thenReturn(new int[]{79});
        MotorOutputCommand testClass = new MotorOutputCommand(mockedPortHandler);

        int[] testResponse = testClass.run(sendTest,1);

        Assertions.assertArrayEquals(compareResponse, testResponse);

    }

    @Test
    public void runMessageType2Test() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        int[] compareResponse = new int[] {10, 0, 17, 180,4,0,32,9,8,90,91,185};
        int[] sendTest = new int[]{8, 0, 32, 176, 4, 0, 17, 9, 8, 254};

        PortHandler mockedPortHandler = mock(PortHandler.class);
        when(mockedPortHandler.writeSerialPortBytes(sendTest)).thenReturn(true);
        when(mockedPortHandler.readSerialPortBytes(1,false, Constants.MOTOR_CONTROLLER_TIMEOUT)).thenReturn(new int[] {79}).thenReturn(new int[] {10});
        when(mockedPortHandler.readSerialPortBytes(11,true, Constants.MOTOR_CONTROLLER_TIMEOUT)).thenReturn(new int[] {0,17,180,4,0,32,9,8,90,91,185});
        MotorOutputCommand testClass = new MotorOutputCommand(mockedPortHandler);

        int[] testResponse = testClass.run(sendTest,2);

        Assertions.assertArrayEquals(compareResponse, testResponse);

    }

    @Test
    public void runMessageType3ResponseTest() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        int[] compareResponse = new int[] {MotorControllerConstants.MOTOR_CONTROLLER_INITIAL_RESPONSE};
        int[] sendTest = new int[]{MotorControllerConstants.MOTOR_CONTROLLER_INITIAL_REQUEST};

        PortHandler mockedPortHandler = mock(PortHandler.class);
        when(mockedPortHandler.writeSerialPortBytes(sendTest)).thenReturn(true);
        when(mockedPortHandler.readSerialPortBytes(1,false, Constants.MOTOR_CONTROLLER_TIMEOUT)).thenReturn(new int[] {MotorControllerConstants.MOTOR_CONTROLLER_INITIAL_RESPONSE});
        MotorOutputCommand testClass = new MotorOutputCommand(mockedPortHandler);

        int[] testResponse = testClass.run(sendTest,3);

        Assertions.assertArrayEquals(compareResponse, testResponse);

    }

    @Test
    public void runMessageType3NoResponseTest() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        int[] sendTest = new int[]{MotorControllerConstants.EXPECTED_RESPONSE_FROM_COMMAND};

        PortHandler mockedPortHandler = mock(PortHandler.class);
        when(mockedPortHandler.writeSerialPortBytes(sendTest)).thenReturn(true);
        when(mockedPortHandler.readSerialPortBytes(2,true, Constants.MOTOR_CONTROLLER_TIMEOUT)).thenThrow(new SerialPortTimeoutException("Test Port","Test Method",100));
        MotorOutputCommand testClass = new MotorOutputCommand(mockedPortHandler);

        int[] testResponse = testClass.run(sendTest,3);

        Assertions.assertNull(testResponse);

    }

    @Test
    public void runMessageIllegalArgExceptionTest() throws SerialPortException, SerialPortTimeoutException {
        int[] sendTest = new int[]{79};

        PortHandler mockedPortHandler = mock(PortHandler.class);
        when(mockedPortHandler.writeSerialPortBytes(sendTest)).thenReturn(true);
        when(mockedPortHandler.readSerialPortBytes(1,false, Constants.MOTOR_CONTROLLER_TIMEOUT)).thenReturn(new int[] {79}).thenReturn(new int[] {12});

        MotorOutputCommand testClass = new MotorOutputCommand(mockedPortHandler);

        Assertions.assertThrows(IllegalArgumentException.class, () -> testClass.run(sendTest,20));
    }

    @Test
    public void runIncorrectResponseFromSerialPortTest() throws SerialPortException, SerialPortTimeoutException {
        int[] sendTest = new int[]{MotorControllerConstants.EXPECTED_RESPONSE_FROM_COMMAND};

        PortHandler mockedPortHandler = mock(PortHandler.class);
        when(mockedPortHandler.writeSerialPortBytes(sendTest)).thenReturn(true);
        when(mockedPortHandler.readSerialPortBytes(1,false, Constants.MOTOR_CONTROLLER_TIMEOUT)).thenReturn(new int[] {10});

        SerialPort mockedSerialPort = mock(SerialPort.class);
        when(mockedSerialPort.getPortName()).thenReturn("TestPort");
        when(mockedPortHandler.getSerialPort()).thenReturn(mockedSerialPort);

        MotorOutputCommand testClass = new MotorOutputCommand(mockedPortHandler);

        Assertions.assertThrows(IncorrectResponseFromSerialPortException.class, () -> testClass.run(sendTest,20));
    }

}
