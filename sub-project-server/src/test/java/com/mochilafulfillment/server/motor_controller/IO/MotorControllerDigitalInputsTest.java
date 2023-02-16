package com.mochilafulfillment.server.motor_controller.IO;

import com.mochilafulfillment.server.motor_controller.MotorControllerConstants;
import com.mochilafulfillment.server.motor_controller.dtos.MotorControllerRecord;
import com.mochilafulfillment.server.communications.serial_port.PortHandler;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class MotorControllerDigitalInputsTest {
    MotorControllerDigitalInputs testClass;
    SerialPort mockedPort;
    PortHandler portHandler;

    @BeforeEach
    public void init(){
        mockedPort = mock(SerialPort.class);
        portHandler = new PortHandler(mockedPort);
        testClass = new MotorControllerDigitalInputs(portHandler);
        testClass = spy(testClass);
    }

    @Test
    public void createInputStatusCommand1Test(){
        int[] testBytes = testClass.createInputStatusCallAxis1();
        int[] responseBytes = {0X08, 0X00, 0X10, 0XB0, 0X04, 0X00, 0X11, 0X09, 0X08, 0XEE};
        Assertions.assertArrayEquals(testBytes,responseBytes);
    }

    @Test
    public void createInputStatusCommand2Test(){
        int[] testBytes = testClass.createInputStatusCallAxis2();
        int[] responseBytes = {0X08, 0X00, 0X20, 0XB0, 0X04, 0X00, 0X11, 0X09, 0X08, 0XFE};
        Assertions.assertArrayEquals(testBytes,responseBytes);
    }

    @Test
    public void byteToBooleanTrueTest(){
        boolean[] testBoolean = testClass.byteToBoolean(15,15);
        boolean[] compareBoolean = new boolean[]{true, true, true, false};
        Assertions.assertArrayEquals(testBoolean, compareBoolean);
    }

    @Test
    public void byteToBooleanFalseTest(){
        boolean[] testBoolean = testClass.byteToBoolean(0,0);
        boolean[] compareBoolean = new boolean[]{false, false, false, true};
        Assertions.assertArrayEquals(testBoolean, compareBoolean);
    }

    @Test
    public void byteToBooleanMixTest(){
        boolean[] testBoolean = testClass.byteToBoolean(4,8);
        boolean[] compareBoolean = new boolean[]{true, false, false ,false};
        Assertions.assertArrayEquals(testBoolean, compareBoolean);
    }

    @Test
    public void filterInputResponseTest(){
        int[] testBytes1 = new int[]{MotorControllerConstants.EXPECTED_RESPONSE_FROM_COMMAND, 0X0A, 0X00, 0X11, 0XB4, 0X00, 0X00,
                0X00, 0X00, 0X00, 0X00, 0X0C, 0x00};
        int checkSum1 = IOUtils.checkSum(testBytes1);
        testBytes1[12] = checkSum1;

        int[] intTestBytes1 = new int[testBytes1.length];
        for (int i= 0; i < testBytes1.length; i++) {
            intTestBytes1[i] = testBytes1[i];
        }

        int[] testBytes2 = new int[]{MotorControllerConstants.EXPECTED_RESPONSE_FROM_COMMAND, 0X0A, 0X00, 0X11, 0XB4, 0X00, 0X00,
                0X00, 0X00, 0X00, 0X60, 0X04, 0x00};
        int checkSum2 = IOUtils.checkSum(testBytes2);
        testBytes2[12] = checkSum2;

        int[] intTestBytes2 = new int[testBytes2.length];
        for (int i= 0; i < testBytes2.length; i++) {
            intTestBytes2[i] = testBytes2[i];
        }

        boolean[] inputStatus = testClass.filterInputResponse(intTestBytes1, intTestBytes2);
        Assertions.assertArrayEquals(inputStatus, new boolean[] {true, true, true, true});
    }

    @Test
    public void checkTopSensorTriggeredTest() throws SerialPortException, SerialPortTimeoutException, InterruptedException {
        MotorControllerRecord testRecord = new MotorControllerRecord();
        doReturn(new boolean[]{false, false, true, false}).when(testClass).getInputStatus();
        testRecord = testClass.check(testRecord);

        MotorControllerRecord compareRecord = new MotorControllerRecord();
        compareRecord.setTopLiftSensor(true);
        Assertions.assertEquals(testRecord, compareRecord);
    }

    @Test
    public void checkMiddleSensorTriggeredTest() throws SerialPortException, SerialPortTimeoutException, InterruptedException {
        MotorControllerRecord testRecord = new MotorControllerRecord();
        doReturn(new boolean[]{false, true, false, false}).when(testClass).getInputStatus();
        testRecord = testClass.check(testRecord);

        MotorControllerRecord compareRecord = new MotorControllerRecord();
        compareRecord.setMiddleLiftSensor(true);
        Assertions.assertEquals(testRecord, compareRecord);
    }

    @Test
    public void checkBottomSensorTriggeredTest() throws SerialPortException, SerialPortTimeoutException, InterruptedException {
        MotorControllerRecord testRecord = new MotorControllerRecord();
        doReturn(new boolean[]{true, false, false, false}).when(testClass).getInputStatus();
        testRecord = testClass.check(testRecord);

        MotorControllerRecord compareRecord = new MotorControllerRecord();
        compareRecord.setBottomLiftSensor(true);
        Assertions.assertEquals(testRecord, compareRecord);
    }

    @Test
    public void checkWarningZoneTriggeredTest() throws SerialPortException, SerialPortTimeoutException, InterruptedException {
        MotorControllerRecord testRecord = new MotorControllerRecord();
        doReturn(new boolean[]{false, false, false, true}).when(testClass).getInputStatus();
        testRecord = testClass.check(testRecord);

        MotorControllerRecord compareRecord = new MotorControllerRecord();
        compareRecord.setWarningZone(true);
        Assertions.assertEquals(testRecord, compareRecord);
    }
}
