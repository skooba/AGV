package com.mochilafulfillment.server.motor_controller.IO;

import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.motor_controller.MotorOutputCommand;
import com.mochilafulfillment.server.communications.serial_port.PortHandler;
import com.mochilafulfillment.server.motor_controller.dtos.MotorControllerRecord;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class MotorControllerDigitalOutputsTest {

    SerialPort mockedPort;
    PortHandler portHandler;
    MotorControllerDigitalOutputs testClass;

    @BeforeEach
    public void init(){
        mockedPort = mock(SerialPort.class);
        portHandler = new PortHandler(mockedPort);
        testClass = new MotorControllerDigitalOutputs(portHandler);
    }

    @Test
    public void liftUpBytesTest(){
        int[] testBytes = testClass.createLiftUpBytes();
        int[] responseBytes = {0X08, 0X00, 0X20, 0XEC, 0X00, 0X00, 0X04, 0X00, 0X00, 0X18};
        Assertions.assertArrayEquals(testBytes,responseBytes);
    }

    @Test
    public void liftUpStopBytesTest(){
        int[] testBytes = testClass.createLiftUpStopBytes();
        int[] responseBytes = {0X08, 0X00, 0X20, 0XEC, 0X00, 0X00, 0X04, 0X00, 0X04, 0X1C};
        Assertions.assertArrayEquals(testBytes,responseBytes);
    }

    @Test
    public void liftDownBytesTest(){
        int[] testBytes = testClass.createLiftDownBytes();
        int[] responseBytes = {0X08, 0X00, 0X20, 0XEC, 0X00, 0X00, 0X08, 0X00, 0X00, 0X1C};
        Assertions.assertArrayEquals(testBytes,responseBytes);
    }

    @Test
    public void liftDownStopBytesTest(){
        int[] testBytes = testClass.createLiftDownStopBytes();
        int[] responseBytes = {0X08, 0X00, 0X20, 0XEC, 0X00, 0X00, 0X08, 0X00, 0X08, 0X24};
        Assertions.assertArrayEquals(testBytes,responseBytes);
    }

    @Test
    public void createScanner1OnTest(){
        int[] testBytes = testClass.createScanner1On();
        int[] responseBytes = {0X08, 0X00, 0X10, 0XEC, 0X00, 0X00, 0X04, 0X00, 0X04, 0X0C};
        Assertions.assertArrayEquals(testBytes,responseBytes);
    }

    @Test
    public void createScanner1OffTest(){
        int[] testBytes = testClass.createScanner1Off();
        int[] responseBytes = {0X08, 0X00, 0X10, 0XEC, 0X00, 0X00, 0X04, 0X00, 0X00, 0X08};
        Assertions.assertArrayEquals(testBytes,responseBytes);
    }

    @Test
    public void createScanner2OnTest(){
        int[] testBytes = testClass.createScanner2On();
        int[] responseBytes = {0X08, 0X00, 0X10, 0XEC, 0X00, 0X00, 0X08, 0X00, 0X08, 0X14};
        Assertions.assertArrayEquals(testBytes,responseBytes);
    }

    @Test
    public void createScanner2OffTest(){
        int[] testBytes = testClass.createScanner2Off();
        int[] responseBytes = {0X08, 0X00, 0X10, 0XEC, 0X00, 0X00, 0X08, 0X00, 0X00, 0X0C};
        Assertions.assertArrayEquals(testBytes,responseBytes);
    }

    @Test
    public void setLiftUpTest() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        MotorOutputCommand mockMotorOutputs = mock(MotorOutputCommand.class);

        testClass.setMotorControllerWrite(mockMotorOutputs);
        testClass.setLiftUp();

        verify(mockMotorOutputs).run(testClass.createLiftDownStopBytes(), 1);
        verify(mockMotorOutputs).run(testClass.createLiftUpBytes(), 1);
    }

    @Test
    public void setLiftDownTest() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        MotorOutputCommand mockMotorOutputs = mock(MotorOutputCommand.class);

        testClass.setMotorControllerWrite(mockMotorOutputs);
        testClass.setLiftDown();

        verify(mockMotorOutputs).run(testClass.createLiftDownBytes(), 1);
        verify(mockMotorOutputs).run(testClass.createLiftUpStopBytes(), 1);
    }

    @Test
    public void setLiftStopTest() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        MotorOutputCommand mockMotorOutputs = mock(MotorOutputCommand.class);

        testClass.setMotorControllerWrite(mockMotorOutputs);
        testClass.setLiftStop();

        verify(mockMotorOutputs).run(testClass.createLiftDownStopBytes(), 1);
        verify(mockMotorOutputs).run(testClass.createLiftUpStopBytes(), 1);
    }

    @Test
    public void setScannerNormal() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        MotorOutputCommand mockMotorOutputs = mock(MotorOutputCommand.class);

        testClass.setMotorControllerWrite(mockMotorOutputs);
        testClass.setScannerNormal();

        verify(mockMotorOutputs).run(testClass.createScanner1On(), 1);
        verify(mockMotorOutputs).run(testClass.createScanner2Off(), 1);
    }

    @Test
    public void setScannerPicking() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        MotorOutputCommand mockMotorOutputs = mock(MotorOutputCommand.class);

        testClass.setMotorControllerWrite(mockMotorOutputs);
        testClass.setScannerPicking();

        verify(mockMotorOutputs).run(testClass.createScanner1Off(), 1);
        verify(mockMotorOutputs).run(testClass.createScanner2On(), 1);
    }

    @Test
    public void setLiftStopWriteTest() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        MotorControllerRecord motorControllerRecord = new MotorControllerRecord();
        MotorControllerRecord oldControllerRecord = new MotorControllerRecord();

        oldControllerRecord.setLiftType(Constants.LIFT_CONSTANT);
        motorControllerRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);

        MotorControllerDigitalOutputs motorControllerDigitalOutputs = new MotorControllerDigitalOutputs(portHandler);
        MotorControllerDigitalOutputs spyMotorControllerDigitalOutputs = spy(motorControllerDigitalOutputs);

        doNothing().when(spyMotorControllerDigitalOutputs).setLiftStop();
        doNothing().when(spyMotorControllerDigitalOutputs).setLiftUp();
        doNothing().when(spyMotorControllerDigitalOutputs).setLiftDown();
        doNothing().when(spyMotorControllerDigitalOutputs).setScannerNormal();
        doNothing().when(spyMotorControllerDigitalOutputs).setScannerPicking();

        spyMotorControllerDigitalOutputs.write(motorControllerRecord,oldControllerRecord);

        verify(spyMotorControllerDigitalOutputs).setLiftStop();
        verify(spyMotorControllerDigitalOutputs, never()).setLiftUp();
        verify(spyMotorControllerDigitalOutputs, never()).setLiftDown();
        verify(spyMotorControllerDigitalOutputs, never()).setScannerNormal();
        verify(spyMotorControllerDigitalOutputs, never()).setScannerPicking();
    }

    @Test
    public void setLiftUpWriteTest() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        MotorControllerRecord motorControllerRecord = new MotorControllerRecord();
        MotorControllerRecord oldControllerRecord = new MotorControllerRecord();

        oldControllerRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);
        motorControllerRecord.setLiftType(Constants.LIFT_CONSTANT);

        MotorControllerDigitalOutputs motorControllerDigitalOutputs = new MotorControllerDigitalOutputs(portHandler);
        MotorControllerDigitalOutputs spyMotorControllerDigitalOutputs = spy(motorControllerDigitalOutputs);

        doNothing().when(spyMotorControllerDigitalOutputs).setLiftStop();
        doNothing().when(spyMotorControllerDigitalOutputs).setLiftUp();
        doNothing().when(spyMotorControllerDigitalOutputs).setLiftDown();
        doNothing().when(spyMotorControllerDigitalOutputs).setScannerNormal();
        doNothing().when(spyMotorControllerDigitalOutputs).setScannerPicking();

        spyMotorControllerDigitalOutputs.write(motorControllerRecord,oldControllerRecord);

        verify(spyMotorControllerDigitalOutputs, never()).setLiftStop();
        verify(spyMotorControllerDigitalOutputs).setLiftUp();
        verify(spyMotorControllerDigitalOutputs, never()).setLiftDown();
        verify(spyMotorControllerDigitalOutputs, never()).setScannerNormal();
        verify(spyMotorControllerDigitalOutputs, never()).setScannerPicking();
    }

    @Test
    public void setLiftDownWriteTest() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        MotorControllerRecord motorControllerRecord = new MotorControllerRecord();
        MotorControllerRecord oldControllerRecord = new MotorControllerRecord();

        oldControllerRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);
        motorControllerRecord.setLiftType(Constants.LOWER_CONSTANT);

        MotorControllerDigitalOutputs motorControllerDigitalOutputs = new MotorControllerDigitalOutputs(portHandler);
        MotorControllerDigitalOutputs spyMotorControllerDigitalOutputs = spy(motorControllerDigitalOutputs);

        doNothing().when(spyMotorControllerDigitalOutputs).setLiftStop();
        doNothing().when(spyMotorControllerDigitalOutputs).setLiftUp();
        doNothing().when(spyMotorControllerDigitalOutputs).setLiftDown();
        doNothing().when(spyMotorControllerDigitalOutputs).setScannerNormal();
        doNothing().when(spyMotorControllerDigitalOutputs).setScannerPicking();

        spyMotorControllerDigitalOutputs.write(motorControllerRecord,oldControllerRecord);

        verify(spyMotorControllerDigitalOutputs, never()).setLiftStop();
        verify(spyMotorControllerDigitalOutputs, never()).setLiftUp();
        verify(spyMotorControllerDigitalOutputs).setLiftDown();
        verify(spyMotorControllerDigitalOutputs, never()).setScannerNormal();
        verify(spyMotorControllerDigitalOutputs, never()).setScannerPicking();
    }

    @Test
    public void setScannerNormalWriteTest() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        MotorControllerRecord motorControllerRecord = new MotorControllerRecord();
        MotorControllerRecord oldControllerRecord = new MotorControllerRecord();

        oldControllerRecord.setSafetyScannerMode(Constants.PICKING_SCAN);
        motorControllerRecord.setSafetyScannerMode(Constants.REGULAR_SCAN);

        MotorControllerDigitalOutputs motorControllerDigitalOutputs = new MotorControllerDigitalOutputs(portHandler);
        MotorControllerDigitalOutputs spyMotorControllerDigitalOutputs = spy(motorControllerDigitalOutputs);

        doNothing().when(spyMotorControllerDigitalOutputs).setLiftStop();
        doNothing().when(spyMotorControllerDigitalOutputs).setLiftUp();
        doNothing().when(spyMotorControllerDigitalOutputs).setLiftDown();
        doNothing().when(spyMotorControllerDigitalOutputs).setScannerNormal();
        doNothing().when(spyMotorControllerDigitalOutputs).setScannerPicking();

        spyMotorControllerDigitalOutputs.write(motorControllerRecord,oldControllerRecord);

        verify(spyMotorControllerDigitalOutputs, never()).setLiftStop();
        verify(spyMotorControllerDigitalOutputs, never()).setLiftUp();
        verify(spyMotorControllerDigitalOutputs, never()).setLiftDown();
        verify(spyMotorControllerDigitalOutputs).setScannerNormal();
        verify(spyMotorControllerDigitalOutputs, never()).setScannerPicking();
    }

    @Test
    public void setScannerPickingWriteTest() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        MotorControllerRecord motorControllerRecord = new MotorControllerRecord();
        MotorControllerRecord oldControllerRecord = new MotorControllerRecord();

        oldControllerRecord.setSafetyScannerMode(Constants.REGULAR_SCAN);
        motorControllerRecord.setSafetyScannerMode(Constants.PICKING_SCAN);

        MotorControllerDigitalOutputs motorControllerDigitalOutputs = new MotorControllerDigitalOutputs(portHandler);
        MotorControllerDigitalOutputs spyMotorControllerDigitalOutputs = spy(motorControllerDigitalOutputs);

        doNothing().when(spyMotorControllerDigitalOutputs).setLiftStop();
        doNothing().when(spyMotorControllerDigitalOutputs).setLiftUp();
        doNothing().when(spyMotorControllerDigitalOutputs).setLiftDown();
        doNothing().when(spyMotorControllerDigitalOutputs).setScannerNormal();
        doNothing().when(spyMotorControllerDigitalOutputs).setScannerPicking();

        spyMotorControllerDigitalOutputs.write(motorControllerRecord,oldControllerRecord);

        verify(spyMotorControllerDigitalOutputs, never()).setLiftStop();
        verify(spyMotorControllerDigitalOutputs, never()).setLiftUp();
        verify(spyMotorControllerDigitalOutputs, never()).setLiftDown();
        verify(spyMotorControllerDigitalOutputs, never()).setScannerNormal();
        verify(spyMotorControllerDigitalOutputs).setScannerPicking();
    }

    @Test
    public void noChangesWriteTest() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        MotorControllerRecord motorControllerRecord = new MotorControllerRecord();
        MotorControllerRecord oldControllerRecord = new MotorControllerRecord();

        MotorControllerDigitalOutputs motorControllerDigitalOutputs = new MotorControllerDigitalOutputs(portHandler);
        MotorControllerDigitalOutputs spyMotorControllerDigitalOutputs = spy(motorControllerDigitalOutputs);

        doNothing().when(spyMotorControllerDigitalOutputs).setLiftStop();
        doNothing().when(spyMotorControllerDigitalOutputs).setLiftUp();
        doNothing().when(spyMotorControllerDigitalOutputs).setLiftDown();
        doNothing().when(spyMotorControllerDigitalOutputs).setScannerNormal();
        doNothing().when(spyMotorControllerDigitalOutputs).setScannerPicking();

        spyMotorControllerDigitalOutputs.write(motorControllerRecord,oldControllerRecord);

        verify(spyMotorControllerDigitalOutputs, never()).setLiftStop();
        verify(spyMotorControllerDigitalOutputs, never()).setLiftUp();
        verify(spyMotorControllerDigitalOutputs, never()).setLiftDown();
        verify(spyMotorControllerDigitalOutputs, never()).setScannerNormal();
        verify(spyMotorControllerDigitalOutputs, never()).setScannerPicking();
    }
}
