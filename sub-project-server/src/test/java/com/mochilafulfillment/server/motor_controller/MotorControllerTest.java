package com.mochilafulfillment.server.motor_controller;

import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.motor_controller.IO.MotorControllerDigitalInputs;
import com.mochilafulfillment.server.motor_controller.IO.MotorControllerDigitalOutputs;
import com.mochilafulfillment.server.motor_controller.IO.MotorControllerMotorOutputs;
import com.mochilafulfillment.server.motor_controller.dtos.MotorControllerRecord;
import com.mochilafulfillment.server.communications.serial_port.PortHandler;
import com.mochilafulfillment.shared.SharedConstants;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static org.mockito.Mockito.*;

public class MotorControllerTest {

    MotorController testClass;
    MotorControllerRecord testRecord;

    @BeforeEach
    public void init(){
        testRecord = new MotorControllerRecord();
        testClass = new MotorController("test port", testRecord, new ReentrantLock());
        SerialPort mockedPort = mock(SerialPort.class);
        PortHandler portHandler = new PortHandler(mockedPort);
        testClass.setPortHandler(portHandler);
    }

    @Test
    public void stopAllMovementTest() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        MotorControllerRecord oldTestRecord = new MotorControllerRecord();

        testRecord.setDirection(Constants.BACKWARDS_STRING);
        testRecord.setMotor1Velocity(Constants.STANDARD_VELOCITY);
        testRecord.setMotor2Velocity(Constants.STANDARD_VELOCITY);
        testRecord.setMotor1Sign(Constants.MOTOR_NEGATIVE);
        testRecord.setMotor2Sign(Constants.MOTOR_NEGATIVE);
        testRecord.setLiftType(Constants.LIFT_CONSTANT);
        testRecord.setNominalAccel(Constants.STANDARD_ACCELERATION);
        testRecord.setSafetyScannerMode(Constants.PICKING_SCAN);
        testRecord.setNewMotorControllerRecord(false);

        MotorControllerMotorOutputs mockedMotorOutputs = mock(MotorControllerMotorOutputs.class);
        testClass.setMotorOutputs(mockedMotorOutputs);


        testClass.stopAllMovement(testRecord, oldTestRecord);

        MotorControllerRecord compareRecord = new MotorControllerRecord();
        compareRecord.setDirection(Constants.BACKWARDS_STRING);
        compareRecord.setMotor1Velocity(0);
        compareRecord.setMotor2Velocity(0);
        compareRecord.setMotor1Sign(Constants.MOTOR_POSITIVE);
        compareRecord.setMotor2Sign(Constants.MOTOR_POSITIVE);
        compareRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);
        compareRecord.setNominalAccel(Constants.STOPPING_ACCELERATION);
        compareRecord.setSafetyScannerMode(Constants.REGULAR_SCAN);
        compareRecord.setNewAgvToMotorControllerRecord(true);

        testRecord = testClass.getControllerRecord();

        Assertions.assertEquals(testRecord, compareRecord);
        verify(mockedMotorOutputs).write(any(), any());
    }

    @Test
    public void stopAllMovementExceptionLessThanCounterTest() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        MotorControllerMotorOutputs mockedMotorOutputs = mock(MotorControllerMotorOutputs.class);
        doThrow(SerialPortTimeoutException.class).when(mockedMotorOutputs).write(any(),any());
        testClass.setMotorOutputs(mockedMotorOutputs);

        Assertions.assertThrows(RuntimeException.class,()->testClass.stopAllMovement(new MotorControllerRecord(), new MotorControllerRecord()));
        Assertions.assertEquals(testClass.getStopMotorCounter(), MotorControllerConstants.NUMBER_OF_STOP_MOTOR_TRIES);
    }

    @Test
    public void updatesToAgvRecordAvailableTest() throws SerialPortException, InterruptedException, SerialPortTimeoutException {
        MotorControllerRecord testRecordNew = new MotorControllerRecord();

        testRecordNew.setBottomLiftSensor(true);
        testRecordNew.setMiddleLiftSensor(false);
        testRecordNew.setTopLiftSensor(false);

        MotorControllerRecord testOldRecord = new MotorControllerRecord();

        testRecordNew.setBottomLiftSensor(false);
        testRecordNew.setMiddleLiftSensor(true);
        testRecordNew.setTopLiftSensor(true);

        int[] desiredResponse = new int[] {MotorControllerConstants.MOTOR_CONTROLLER_INITIAL_RESPONSE};
        PortHandler mockedPortHandler = mock(PortHandler.class);
        when(mockedPortHandler.writeSerialPortBytes(any())).thenReturn(true);
        when(mockedPortHandler.readSerialPortBytes(1,false, Constants.MOTOR_CONTROLLER_TIMEOUT)).thenReturn(desiredResponse);
        testClass.setPortHandler(mockedPortHandler);

        MotorControllerDigitalOutputs mockedDigitalOutputRecord = mock(MotorControllerDigitalOutputs.class);
        testClass.setDigitalOutputs(mockedDigitalOutputRecord);

        MotorControllerDigitalInputs mockedDigitalInputs = mock(MotorControllerDigitalInputs.class);
        when(mockedDigitalInputs.check(any())).thenReturn(testRecordNew);
        testClass.setDigitalInputs(mockedDigitalInputs);

        MotorControllerMotorOutputs mockedMotorOutputs = mock(MotorControllerMotorOutputs.class);
        when(mockedMotorOutputs.initialize()).thenReturn(true);
        testClass.setMotorOutputs(mockedMotorOutputs);

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(testClass);
        executor.awaitTermination(SharedConstants.TEST_RUNTIME, TimeUnit.MILLISECONDS);

        verify(mockedDigitalOutputRecord, atLeastOnce()).write(testRecordNew, testOldRecord);
        testRecord = testClass.getControllerRecord();
        Assertions.assertEquals(testRecord.isNewMotorControllerRecord(), true);
    }

    @Test
    public void updatesToAgvRecordUnavailableTest() throws SerialPortException, InterruptedException, SerialPortTimeoutException {
        MotorControllerRecord testRecordNew = new MotorControllerRecord();
        MotorControllerRecord testRecordOld = new MotorControllerRecord();

        int[] desiredResponse = new int[] {MotorControllerConstants.MOTOR_CONTROLLER_INITIAL_RESPONSE};
        PortHandler mockedPortHandler = mock(PortHandler.class);
        when(mockedPortHandler.writeSerialPortBytes(any())).thenReturn(true);
        when(mockedPortHandler.readSerialPortBytes(1,false, Constants.MOTOR_CONTROLLER_TIMEOUT)).thenReturn(desiredResponse);
        testClass.setPortHandler(mockedPortHandler);

        MotorControllerDigitalOutputs mockedDigitalOutputRecord = mock(MotorControllerDigitalOutputs.class);
        testClass.setDigitalOutputs(mockedDigitalOutputRecord);

        MotorControllerDigitalInputs mockedDigitalInputs = mock(MotorControllerDigitalInputs.class);
        when(mockedDigitalInputs.check(testRecord)).thenReturn(testRecordNew);
        testClass.setDigitalInputs(mockedDigitalInputs);

        MotorControllerMotorOutputs mockedMotorOutputs = mock(MotorControllerMotorOutputs.class);
        when(mockedMotorOutputs.initialize()).thenReturn(true);
        testClass.setMotorOutputs(mockedMotorOutputs);

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(testClass);
        executor.awaitTermination(SharedConstants.TEST_RUNTIME, TimeUnit.MILLISECONDS);

        verify(mockedDigitalOutputRecord, never()).write(testRecordNew, testRecordOld);
        testRecord = testClass.getControllerRecord();
        Assertions.assertEquals(testRecord.isNewMotorControllerRecord(), false);
    }

    @Test
    public void motorOutputsWriteTest() throws SerialPortException, InterruptedException, SerialPortTimeoutException {
        MotorControllerRecord newTestRecord = new MotorControllerRecord();
        newTestRecord.setNewAgvToMotorControllerRecord(true);
        testClass.setMotorControllerRecord(newTestRecord);

        MotorControllerDigitalOutputs mockedDigitalOutputRecord = mock(MotorControllerDigitalOutputs.class);
        testClass.setDigitalOutputs(mockedDigitalOutputRecord);

        MotorControllerDigitalInputs mockedDigitalInputs = mock(MotorControllerDigitalInputs.class);
        when(mockedDigitalInputs.check(newTestRecord)).thenReturn(newTestRecord);
        testClass.setDigitalInputs(mockedDigitalInputs);

        MotorControllerMotorOutputs mockedMotorOutputs = mock(MotorControllerMotorOutputs.class);
        testClass.setMotorOutputs(mockedMotorOutputs);

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(testClass);
        executor.awaitTermination(SharedConstants.TEST_RUNTIME, TimeUnit.MILLISECONDS);

        testRecord = testClass.getControllerRecord();
        MotorControllerRecord oldRecord = testClass.getOldControllerRecord();

        verify(mockedMotorOutputs).write(newTestRecord, oldRecord);
//        verify(testRecord, times(1)).setNewMotorControllerRecord(false);
        Assertions.assertEquals(testRecord.isNewAgvToMotorControllerRecord(), false);
    }


    @Test
    public void motorOutputsDoNotWriteTest() throws SerialPortException, InterruptedException, SerialPortTimeoutException {
        MotorControllerRecord newTestRecord = new MotorControllerRecord();
        newTestRecord.setNewAgvToMotorControllerRecord(false);
        testClass.setMotorControllerRecord(newTestRecord);

        MotorControllerDigitalOutputs mockedDigitalOutputRecord = mock(MotorControllerDigitalOutputs.class);
        testClass.setDigitalOutputs(mockedDigitalOutputRecord);

        MotorControllerDigitalInputs mockedDigitalInputs = mock(MotorControllerDigitalInputs.class);
        when(mockedDigitalInputs.check(newTestRecord)).thenReturn(newTestRecord);
        testClass.setDigitalInputs(mockedDigitalInputs);

        MotorControllerMotorOutputs mockedMotorOutputs = mock(MotorControllerMotorOutputs.class);
        testClass.setMotorOutputs(mockedMotorOutputs);

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(testClass);
        executor.awaitTermination(SharedConstants.TEST_RUNTIME, TimeUnit.MILLISECONDS);

        testRecord = testClass.getControllerRecord();

        verify(mockedMotorOutputs, never()).write(newTestRecord, newTestRecord);
        Assertions.assertEquals(testRecord.isNewMotorControllerRecord(), false);

    }

    @Test
    public void warningZoneTest() throws InterruptedException, SerialPortTimeoutException, SerialPortException {
        MotorControllerRecord newTestRecord = new MotorControllerRecord();
        MotorControllerRecord spyTestRecord = spy(newTestRecord);
        spyTestRecord.setWarningZone(true);
        testClass.setMotorControllerRecord(spyTestRecord);

        MotorControllerDigitalInputs testDigitalInputs = testClass.getDigitalInputs();
        MotorControllerDigitalInputs spyDigitalInputs = spy(testDigitalInputs);
        testClass.setDigitalInputs(spyDigitalInputs);

        MotorControllerMotorOutputs mockedMotorOutputs = mock(MotorControllerMotorOutputs.class);
        testClass.setMotorOutputs(mockedMotorOutputs);

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(testClass);
        executor.awaitTermination(SharedConstants.TEST_RUNTIME, TimeUnit.MILLISECONDS);

        testRecord = testClass.getControllerRecord();

        verify(spyTestRecord, atLeastOnce()).setStopped();
        verify(spyDigitalInputs).check(any());


    }

    @Test
    public void errorInRunTest() throws SerialPortTimeoutException, SerialPortException, InterruptedException {

        MotorControllerDigitalInputs mockedDigitalInputs = mock(MotorControllerDigitalInputs.class);

        when(mockedDigitalInputs.check(any())).thenThrow(new SerialPortException("test", "testMethod", "testException"));
        testClass.setDigitalInputs(mockedDigitalInputs);
        Assertions.assertThrows(RuntimeException.class, ()->testClass.run());
    }

    @Test
    public void errorInRunStopAllMovementTest() throws SerialPortTimeoutException, SerialPortException, InterruptedException {

        MotorControllerDigitalInputs mockedDigitalInputs = mock(MotorControllerDigitalInputs.class);

        when(mockedDigitalInputs.check(any())).thenThrow(new SerialPortException("test", "testMethod", "testException"));
        testClass.setDigitalInputs(mockedDigitalInputs);

        MotorControllerRecord controllerRecord = new MotorControllerRecord();
        controllerRecord.setDirection(Constants.FORWARDS_STRING);
        controllerRecord.setMotor1Velocity(50);
        controllerRecord.setMotor2Velocity(50);
        controllerRecord.setMotor1Sign(Constants.MOTOR_NEGATIVE);
        controllerRecord.setMotor2Sign(Constants.MOTOR_NEGATIVE);
        controllerRecord.setLiftType(Constants.LIFT_CONSTANT);
        controllerRecord.setNominalAccel(50);
        controllerRecord.setSafetyScannerMode(Constants.PICKING_SCAN);
        controllerRecord.setNewMotorControllerRecord(false);

        testClass.setMotorControllerRecord(controllerRecord);

        MotorControllerMotorOutputs mockedMotorOutputs = mock(MotorControllerMotorOutputs.class);
        testClass.setMotorOutputs(mockedMotorOutputs);

        MotorController spyTestClass = spy(testClass);

        Assertions.assertThrows(RuntimeException.class, ()->spyTestClass.run());

        verify(spyTestClass).stopAllMovement(any(), any());
    }

}
