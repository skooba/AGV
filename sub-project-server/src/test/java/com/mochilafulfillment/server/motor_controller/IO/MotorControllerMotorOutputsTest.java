package com.mochilafulfillment.server.motor_controller.IO;

import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.communications.serial_port.IncorrectResponseFromSerialPortException;
import com.mochilafulfillment.server.motor_controller.MotorControllerConstants;
import com.mochilafulfillment.server.motor_controller.MotorOutputCommand;
import com.mochilafulfillment.server.motor_controller.dtos.MotorControllerRecord;
import com.mochilafulfillment.server.communications.serial_port.PortHandler;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.mockito.Mockito.*;

public class MotorControllerMotorOutputsTest {

    MotorControllerMotorOutputs testClass;

    @BeforeEach
    public void init(){
        PortHandler mockedPortHandler = mock(PortHandler.class);
        testClass = new MotorControllerMotorOutputs(mockedPortHandler);
    }

    @Test
    public void VelocityAndAccelerationToByteArrayTest(){
        int[] testValue1 = testClass.velocityAndAccelerationToByteArray(0,"");
        int[] testValue2 = testClass.velocityAndAccelerationToByteArray(0,"-");
        int[] testValue3 = testClass.velocityAndAccelerationToByteArray(1,"");
        int[] testValue4 = testClass.velocityAndAccelerationToByteArray(1,"-");
        int[] testValue5 = testClass.velocityAndAccelerationToByteArray(32767,"");
        int[] testValue6 = testClass.velocityAndAccelerationToByteArray(32768,"-");
        int[] testValue7 = testClass.velocityAndAccelerationToByteArray(32768,"");
        int[] testValue8 = testClass.velocityAndAccelerationToByteArray(32769,"-");
        int[] testValue9 = testClass.velocityAndAccelerationToByteArray(65535,"");
        int[] testValue10 = testClass.velocityAndAccelerationToByteArray(32770,"-");
        int[] testValue11 = testClass.velocityAndAccelerationToByteArray(65536,"-");
        int[] testValue12 = testClass.velocityAndAccelerationToByteArray(32986,"");

        Assertions.assertArrayEquals(testValue1, new int[] {0,0,0,0});
        Assertions.assertArrayEquals(testValue2, new int[] {0,0,0,0});
        Assertions.assertArrayEquals(testValue3, new int[] {0,0,0,1});
        Assertions.assertArrayEquals(testValue4, new int[] {0,0,255,255});
        Assertions.assertArrayEquals(testValue5, new int[] {0,0,127,255});
        Assertions.assertArrayEquals(testValue6, new int[] {0,0,128,0});
        Assertions.assertArrayEquals(testValue7, new int[] {128,0,0,0});
        Assertions.assertArrayEquals(testValue8, new int[] {127,255,255,255});
        Assertions.assertArrayEquals(testValue9, new int[] {255,255,0,0});
        Assertions.assertArrayEquals(testValue10, new int[] {127,254,255,255});
        Assertions.assertArrayEquals(testValue11, new int[] {00,00,255,255});
        Assertions.assertArrayEquals(testValue12, new int[] {128,218,0,0});

        Assertions.assertThrows(IllegalArgumentException.class,() -> testClass.velocityAndAccelerationToByteArray(65536,""));
        Assertions.assertThrows(IllegalArgumentException.class,() -> testClass.velocityAndAccelerationToByteArray(65537,"-"));
    }

    @Test
    public void writeAccelTest() throws SerialPortException, SerialPortTimeoutException, InterruptedException {
        MotorOutputCommand mockedOutputs = Mockito.mock(MotorOutputCommand.class);
        testClass.setMotorControllerWrite(mockedOutputs);

        MotorControllerRecord testRecord = new MotorControllerRecord();
        testRecord.setNominalAccel((int)(Math.round(32986d / MotorControllerConstants.ACCEL_MULTIPLIER))); // if ACCEL_MULTIPLIER changes, may have to change fake command by 1 increment
        testRecord.setMotor1Sign("-");
        testRecord.setMotor2Sign("-");

        testClass.writeAccel(testRecord);
        Mockito.verify(mockedOutputs).run(new int[] {8, 0, 16, 36, 162, 128, 216, 0, 0, 54}, 1);
        Mockito.verify(mockedOutputs).run(new int[] {8, 0, 32, 36, 162, 128, 216, 0, 0, 70}, 1);
    }

    @Test
    public void writeVelocityTest() throws SerialPortException, SerialPortTimeoutException, InterruptedException {
        MotorOutputCommand mockedOutputs = Mockito.mock(MotorOutputCommand.class);
        testClass.setMotorControllerWrite(mockedOutputs);

        MotorControllerRecord testRecord = new MotorControllerRecord();
        testRecord.setMotor1Velocity((int)(Math.round(32986 / MotorControllerConstants.VELOCITY_MULTIPLIER)));// if VELOCITY_MULTIPLIER changes, may have to change fake command by 1 increment
        testRecord.setMotor2Velocity((int)(Math.round(32986 / MotorControllerConstants.VELOCITY_MULTIPLIER)));
        testRecord.setMotor1Sign("");
        testRecord.setMotor2Sign("-");

        testClass.writeVelocity1(testRecord);
        testClass.writeVelocity2(testRecord);
        Mockito.verify(mockedOutputs).run(new int[] {8, 0, 16, 36, 160, 128, 216, 0, 0, 52}, 1);
        Mockito.verify(mockedOutputs).run(new int[] {8, 0, 32, 36, 160, 127, 40, 255, 255, 145}, 1);
    }

    @Test
    public void accelWriteTest() throws SerialPortException, SerialPortTimeoutException, InterruptedException {
        MotorControllerMotorOutputs spyTestClass = spy(testClass);
        MotorControllerRecord oldTestRecord = new MotorControllerRecord();
        oldTestRecord.setNominalAccel(10);
        MotorControllerRecord testRecord = new MotorControllerRecord();
        testRecord.setNominalAccel(20);

        MotorOutputCommand mockedMotorOutput = mock(MotorOutputCommand.class);
        spyTestClass.setMotorControllerWrite(mockedMotorOutput);

        spyTestClass.write(testRecord , oldTestRecord);
        verify(spyTestClass).writeAccel(testRecord);
    }

    @Test
    public void velocityWriteTest() throws SerialPortException, SerialPortTimeoutException, InterruptedException {
        MotorControllerMotorOutputs spyTestClass = spy(testClass);
        MotorControllerRecord oldTestRecord = new MotorControllerRecord();
        oldTestRecord.setMotor1Velocity(10);
        MotorControllerRecord testRecord = new MotorControllerRecord();
        testRecord.setMotor1Velocity(20);

        MotorOutputCommand mockedMotorOutput = mock(MotorOutputCommand.class);
        spyTestClass.setMotorControllerWrite(mockedMotorOutput);

        spyTestClass.write(testRecord , oldTestRecord);
        verify(spyTestClass).writeVelocity1(testRecord);
        verify(spyTestClass, never()).writeVelocity2(testRecord);

        MotorControllerRecord oldTestRecord2 = new MotorControllerRecord();
        MotorControllerRecord testRecord2 = new MotorControllerRecord();
        oldTestRecord2.setMotor2Velocity(10);
        testRecord2.setMotor2Velocity(20);

        spyTestClass.write(testRecord2 , oldTestRecord2);
        verify(spyTestClass,never()).writeVelocity1(testRecord2);
        verify(spyTestClass).writeVelocity2(testRecord2);


        MotorControllerRecord oldTestRecord3 = new MotorControllerRecord();
        MotorControllerRecord testRecord3 = new MotorControllerRecord();
        oldTestRecord3.setMotor1Sign(Constants.MOTOR_POSITIVE);
        testRecord3.setMotor1Sign(Constants.MOTOR_NEGATIVE);

        spyTestClass.write(testRecord3 , oldTestRecord3);
        verify(spyTestClass).writeVelocity1(testRecord3);
        verify(spyTestClass,never()).writeVelocity2(testRecord3);


        MotorControllerRecord oldTestRecord4 = new MotorControllerRecord();
        MotorControllerRecord testRecord4 = new MotorControllerRecord();
        oldTestRecord4.setMotor2Sign(Constants.MOTOR_NEGATIVE);
        testRecord4.setMotor2Sign(Constants.MOTOR_POSITIVE);

        spyTestClass.write(testRecord4 , oldTestRecord4);
        verify(spyTestClass, never()).writeVelocity1(testRecord4);
        verify(spyTestClass).writeVelocity2(testRecord4);

        MotorControllerRecord oldTestRecord5 = new MotorControllerRecord();
        MotorControllerRecord testRecord5 = new MotorControllerRecord();
        oldTestRecord5.setMotor1Sign(Constants.MOTOR_POSITIVE);
        testRecord5.setMotor1Sign(Constants.MOTOR_NEGATIVE);
        oldTestRecord5.setMotor2Sign(Constants.MOTOR_NEGATIVE);
        testRecord5.setMotor2Sign(Constants.MOTOR_POSITIVE);

        spyTestClass.write(testRecord5 , oldTestRecord5);
        verify(spyTestClass).writeVelocity1(testRecord5);
        verify(spyTestClass).writeVelocity2(testRecord5);
    }

    @Test
    public void establishCommunicationPassTest() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        int[] responseArray = new int[MotorControllerConstants.INITIALIZATION_TRIES-2];
        Arrays.fill(responseArray, 0);
        int[] initializeMotors2 = new int[]{MotorControllerConstants.MOTOR_CONTROLLER_INITIAL_REQUEST};
        int[] initialResponse = new int[]{MotorControllerConstants.MOTOR_CONTROLLER_INITIAL_RESPONSE};

        MotorOutputCommand motorControllerWrite = mock(MotorOutputCommand.class);
        when(motorControllerWrite.run(initializeMotors2, 3)).thenReturn(new int[]{0},responseArray, initialResponse);

        testClass.setMotorControllerWrite(motorControllerWrite);

        boolean result = testClass.establishCommunication();

        Assertions.assertTrue(result);

    }

    @Test
    public void establishCommunicationFailTest() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        int[] initializeMotors2 = new int[]{MotorControllerConstants.MOTOR_CONTROLLER_INITIAL_REQUEST};

        MotorOutputCommand motorControllerWrite = mock(MotorOutputCommand.class);
        when(motorControllerWrite.run(initializeMotors2, 3)).thenReturn(new int[] {0});

        testClass.setMotorControllerWrite(motorControllerWrite);

        boolean result = testClass.establishCommunication();


        Assertions.assertFalse(result);
    }

    @Test
    public void testControllerPassTest() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        int[] testCommand = new int[]{0x08, 0x00, 0x00, 0xB0, 0x00, 0x00, 0x01, 0x00, 0x00, 0xB9};

        MotorOutputCommand motorControllerWrite = mock(MotorOutputCommand.class);
        when(motorControllerWrite.run(testCommand, 1)).thenReturn(testCommand);

        PortHandler mockedPortHandler = mock(PortHandler.class);
        when(mockedPortHandler.readSerialPortBytes(12, true, Constants.MOTOR_CONTROLLER_TIMEOUT)).thenReturn(new int[] {MotorControllerConstants.SECOND_BYTE_EXPECTED_RESPONSE_FROM_COMMAND});
        testClass = new MotorControllerMotorOutputs(mockedPortHandler);

        testClass.setMotorControllerWrite(motorControllerWrite);

        boolean result = testClass.testController();

        verify(motorControllerWrite).run(MotorControllerConstants.AXIS_1_ON, 1);
        verify(motorControllerWrite).run(MotorControllerConstants.AXIS_2_ON, 1);
        verify(motorControllerWrite).run(MotorControllerConstants.MODE_SP_AXIS_1, 1);
        verify(motorControllerWrite).run(MotorControllerConstants.MODE_SP_AXIS_2, 1);
        Assertions.assertTrue(result);
    }

    @Test
    public void testControllerFailTest() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        int[] testCommand = new int[]{0x08, 0x00, 0x00, 0xB0, 0x00, 0x00, 0x01, 0x00, 0x00, 0xB9};

        MotorOutputCommand motorControllerWrite = mock(MotorOutputCommand.class);
        when(motorControllerWrite.run(testCommand, 1)).thenReturn(testCommand);

        PortHandler mockedPortHandler = mock(PortHandler.class);
        when(mockedPortHandler.readSerialPortBytes(12, true, Constants.MOTOR_CONTROLLER_TIMEOUT)).thenThrow(IncorrectResponseFromSerialPortException.class);
        SerialPort mockedSerialPort = mock(SerialPort.class);
        when(mockedSerialPort.getPortName()).thenReturn("test");
        when(mockedPortHandler.getSerialPort()).thenReturn(mockedSerialPort);
        testClass = new MotorControllerMotorOutputs(mockedPortHandler);

        testClass.setMotorControllerWrite(motorControllerWrite);

        boolean result = testClass.testController();

        Assertions.assertFalse(result);

        verify(motorControllerWrite, times(0)).run(MotorControllerConstants.AXIS_1_ON, 1);
        verify(motorControllerWrite, times(0)).run(MotorControllerConstants.AXIS_2_ON, 1);
        verify(motorControllerWrite, times(0)).run(MotorControllerConstants.MODE_SP_AXIS_1, 1);
        verify(motorControllerWrite, times(0)).run(MotorControllerConstants.MODE_SP_AXIS_2, 1);
    }

    @Test
    public void initializeFirstTryPassTest() throws SerialPortException, SerialPortTimeoutException, InterruptedException {
        PortHandler mockedPortHandler = mock(PortHandler.class);
        SerialPort mockedSerialPort = mock(SerialPort.class);
        when(mockedSerialPort.getPortName()).thenReturn("test");
        when(mockedPortHandler.getSerialPort()).thenReturn(mockedSerialPort);
        testClass = new MotorControllerMotorOutputs(mockedPortHandler);

        MotorControllerMotorOutputs spyTestClass = spy(testClass);
        doReturn(true).when(spyTestClass).testController();
        boolean result = spyTestClass.initialize();

        Assertions.assertTrue(result);
    }

    @Test
    public void initializeSecondTryPassTest() throws SerialPortException, SerialPortTimeoutException, InterruptedException {
        PortHandler mockedPortHandler = mock(PortHandler.class);
        SerialPort mockedSerialPort = mock(SerialPort.class);
        when(mockedSerialPort.getPortName()).thenReturn("test");
        when(mockedPortHandler.getSerialPort()).thenReturn(mockedSerialPort);
        when(mockedPortHandler.getBaudRate()).thenReturn(12345);
        testClass = new MotorControllerMotorOutputs(mockedPortHandler);

        MotorControllerMotorOutputs spyTestClass = spy(testClass);
        doReturn(false, true).when(spyTestClass).testController();
        doReturn(true).when(spyTestClass).establishCommunication();
        boolean result = spyTestClass.initialize();

        Assertions.assertTrue(result);
    }

    @Test
    public void initializeThirdTryPassTest() throws SerialPortException, SerialPortTimeoutException, InterruptedException {
        MotorOutputCommand motorControllerWrite = mock(MotorOutputCommand.class);
        doReturn(new int[0]).when(motorControllerWrite).run(MotorControllerConstants.SET_CONTROLLER_BAUD_TO_115200, 1);

        PortHandler mockedPortHandler = mock(PortHandler.class);
        SerialPort mockedSerialPort = mock(SerialPort.class);
        when(mockedSerialPort.getPortName()).thenReturn("test");
        when(mockedPortHandler.getSerialPort()).thenReturn(mockedSerialPort);
        when(mockedPortHandler.getBaudRate()).thenReturn(12345);
        doNothing().when(mockedPortHandler).changeBaud(anyInt());
        testClass = new MotorControllerMotorOutputs(mockedPortHandler);
        testClass.setMotorControllerWrite(motorControllerWrite);


        MotorControllerMotorOutputs spyTestClass = spy(testClass);
        doReturn(false, true).when(spyTestClass).testController();
        doReturn(false, true).when(spyTestClass).establishCommunication();
        boolean result = spyTestClass.initialize();

        Assertions.assertTrue(result);

        verify(mockedPortHandler,times(1)).changeBaud(MotorControllerConstants.RESTART_BAUD_RATE);
        verify(mockedPortHandler,times(1)).changeBaud(MotorControllerConstants.BAUD_RATE);

    }

    @Test
    public void initializeFailTest() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        MotorOutputCommand motorControllerWrite = mock(MotorOutputCommand.class);
        doReturn(new int[0]).when(motorControllerWrite).run(MotorControllerConstants.SET_CONTROLLER_BAUD_TO_115200, 1);

        PortHandler mockedPortHandler = mock(PortHandler.class);
        SerialPort mockedSerialPort = mock(SerialPort.class);
        when(mockedSerialPort.getPortName()).thenReturn("test");
        when(mockedPortHandler.getSerialPort()).thenReturn(mockedSerialPort);
        when(mockedPortHandler.getBaudRate()).thenReturn(12345);
        doNothing().when(mockedPortHandler).changeBaud(anyInt());
        testClass = new MotorControllerMotorOutputs(mockedPortHandler);
        testClass.setMotorControllerWrite(motorControllerWrite);


        MotorControllerMotorOutputs spyTestClass = spy(testClass);
        doReturn(false).when(spyTestClass).testController();
        doReturn(false).when(spyTestClass).establishCommunication();



        Assertions.assertThrows(IncorrectResponseFromSerialPortException.class,()->spyTestClass.initialize());

        verify(mockedPortHandler,times(1)).changeBaud(MotorControllerConstants.RESTART_BAUD_RATE);

    }
}
