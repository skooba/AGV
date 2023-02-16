package com.mochilafulfillment.server.modes;

import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.api.laptop.TerminalReader;
import com.mochilafulfillment.server.communications.tcp_socket.TcpServer;
import com.mochilafulfillment.server.dtos.AgvOutputsRecord;
import com.mochilafulfillment.server.dtos.RemoteGuiRecord;
import com.mochilafulfillment.server.motor_controller.dtos.MotorControllerRecord;
import com.mochilafulfillment.server.position_scanner.dtos.PositionScannerResponseRecord;
import com.mochilafulfillment.shared.SharedConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class RemoteControlModeTest {

    RemoteControlMode testClass;

    @BeforeEach
    public void init() {
        testClass = new RemoteControlMode(SharedConstants.TCP_PORT);
    }

    @Test
    public void startTCPServerTest() throws InterruptedException {
        RemoteControlMode spyTestClass = spy(new RemoteControlMode(SharedConstants.TCP_PORT));
        doNothing().when(spyTestClass).launchTcpListener();

        TcpServer mockedServer = mock(TcpServer.class);
        spyTestClass.setTcpServer(mockedServer);

        AgvOutputsRecord testRecord = new AgvOutputsRecord();

        spyTestClass.run(testRecord, new PositionScannerResponseRecord(), new MotorControllerRecord(), new TerminalReader());

        verify(mockedServer).writeToClient(SharedConstants.TCP_START_STRING);
        verify(spyTestClass).launchTcpListener();

    }

    @Test
    public void endModeTest(){
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setModeFinished(false);
        testRecord = testClass.endMode(testRecord);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setModeFinished(true);

        Assertions.assertEquals(testRecord,compareRecord);
    }

    @Test
    public void setAGVRecordBasedOnRemoteCommandForksUpTest(){
        RemoteGuiRecord remoteRecord = new RemoteGuiRecord();
        remoteRecord.setForksUp(true);

        testClass.setRemoteRecord(remoteRecord);

        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord = testClass.setAGVRecordBasedOnRemoteCommand(testRecord);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setLiftType(Constants.LIFT_CONSTANT);

        Assertions.assertEquals(testRecord, compareRecord);

    }

    @Test
    public void setAGVRecordBasedOnRemoteCommandForksDownTest(){
        RemoteGuiRecord remoteRecord = new RemoteGuiRecord();
        remoteRecord.setForksDown(true);

        testClass.setRemoteRecord(remoteRecord);

        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord = testClass.setAGVRecordBasedOnRemoteCommand(testRecord);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setLiftType(Constants.LOWER_CONSTANT);

        Assertions.assertEquals(testRecord, compareRecord);
    }

    @Test
    public void setAgvRecordBasedOnRemoteCommandMoveForwardsTest(){
        RemoteGuiRecord remoteRecord = new RemoteGuiRecord();
        remoteRecord.setMoveForward(true);
        remoteRecord.setVelocity(50);
        remoteRecord.setAcceleration(100);

        testClass.setRemoteRecord(remoteRecord);

        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord = testClass.setAGVRecordBasedOnRemoteCommand(testRecord);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setDirection(Constants.FORWARDS_STRING);
        compareRecord.setNominalVelocity(50);
        compareRecord.setNominalAccel(100);

        Assertions.assertEquals(testRecord, compareRecord);
    }

    @Test
    public void setAGVRecordBasedOnRemoteCommandMoveBackwardsTest(){
        RemoteGuiRecord remoteRecord = new RemoteGuiRecord();
        remoteRecord.setMoveBackwards(true);
        remoteRecord.setVelocity(50);
        remoteRecord.setAcceleration(100);

        testClass.setRemoteRecord(remoteRecord);

        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord = testClass.setAGVRecordBasedOnRemoteCommand(testRecord);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setDirection(Constants.BACKWARDS_STRING);
        compareRecord.setNominalVelocity(50);
        compareRecord.setNominalAccel(100);

        Assertions.assertEquals(testRecord, compareRecord);
    }

    @Test
    public void setAGVRecordBasedOnRemoteCommandMoveCWDefaultTest(){
        RemoteGuiRecord remoteRecord = new RemoteGuiRecord();
        remoteRecord.setMoveCW(true);

        testClass.setRemoteRecord(remoteRecord);

        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord = testClass.setAGVRecordBasedOnRemoteCommand(testRecord);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setDirection(Constants.CW_STRING);

        Assertions.assertEquals(testRecord, compareRecord);
    }

    @Test
    public void setAGVRecordBasedOnRemoteCommandMoveCCWDefaultTest(){
        RemoteGuiRecord remoteRecord = new RemoteGuiRecord();
        remoteRecord.setMoveCCW(true);

        testClass.setRemoteRecord(remoteRecord);

        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord = testClass.setAGVRecordBasedOnRemoteCommand(testRecord);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setDirection(Constants.CCW_STRING);

        Assertions.assertEquals(testRecord, compareRecord);
    }

    @Test
    public void setAGVRecordBasedOnRemoteCommandMoveCWCustomVelocityAndAccelTest(){
        RemoteGuiRecord remoteRecord = new RemoteGuiRecord();
        remoteRecord.setMoveCW(true);
        remoteRecord.setRotationVelocity(93);
        remoteRecord.setRotationAcceleration(92);

        testClass.setRemoteRecord(remoteRecord);

        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord = testClass.setAGVRecordBasedOnRemoteCommand(testRecord);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setDirection(Constants.CW_STRING);
        compareRecord.setRotationVelocity(93);
        compareRecord.setRotationAcceleration(92);

        Assertions.assertEquals(testRecord, compareRecord);
    }

    @Test
    public void setAGVRecordBasedOnRemoteCommandMoveCCWCustomVelocityAndAccelTest(){
        RemoteGuiRecord remoteRecord = new RemoteGuiRecord();
        remoteRecord.setMoveCCW(true);
        remoteRecord.setRotationVelocity(83);
        remoteRecord.setRotationAcceleration(82);

        testClass.setRemoteRecord(remoteRecord);

        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord = testClass.setAGVRecordBasedOnRemoteCommand(testRecord);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setDirection(Constants.CCW_STRING);
        compareRecord.setRotationVelocity(83);
        compareRecord.setRotationAcceleration(82);

        Assertions.assertEquals(testRecord, compareRecord);
    }

    @Test
    public void setAGVRecordBasedOnRemoteCommandStopTest(){
        RemoteGuiRecord remoteRecord = new RemoteGuiRecord();

        testClass.setRemoteRecord(remoteRecord);

        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord = testClass.setAGVRecordBasedOnRemoteCommand(testRecord);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setDirection(Constants.STOP_STRING);

        Assertions.assertEquals(testRecord, compareRecord);
    }

    @Test
    public void exitRemoteGuiTest() throws InterruptedException {
        TerminalReader terminalReader = new TerminalReader();
        TerminalReader spyTerminalReader = spy(terminalReader);
        doReturn(true).when(spyTerminalReader).isExitRemoteMode();

        TcpServer mockedServer = mock(TcpServer.class);
        testClass.setTcpServer(mockedServer);

        RemoteControlMode spyTestClass = spy(testClass);
        doNothing().when(spyTestClass).launchTcpListener();

        RemoteGuiRecord remoteTestRecord = new RemoteGuiRecord();
        remoteTestRecord.setClientFinished(false);
        spyTestClass.setRemoteRecord(remoteTestRecord);

        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setModeFinished(false);

        testRecord = spyTestClass.run(testRecord, new PositionScannerResponseRecord(), new MotorControllerRecord(), spyTerminalReader);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setModeFinished(true);

        remoteTestRecord = spyTestClass.getRemoteRecord();

        RemoteGuiRecord compareRemoteRecord = new RemoteGuiRecord();
        compareRemoteRecord.setClientFinished(true);

        Assertions.assertEquals(compareRecord, testRecord);
        Assertions.assertEquals(compareRemoteRecord, remoteTestRecord);
        verify(mockedServer).writeToClient(SharedConstants.TCP_SHUTDOWN_STRING);
        verify(spyTerminalReader).setExitRemoteMode(false);
    }


    @Test
    public void updateAgvRecordBasedOnAgvRecordTest() throws InterruptedException {
        RemoteGuiRecord testRemoteRecord = new RemoteGuiRecord();
        testRemoteRecord.setNewRemoteGuiToAGVRecord(true);
        testRemoteRecord.setMoveForward(true);
        testRemoteRecord.setVelocity(55);
        testRemoteRecord.setAcceleration(45);
        testClass.setRemoteRecord(testRemoteRecord);

        TcpServer mockedServer = mock(TcpServer.class);
        testClass.setTcpServer(mockedServer);

        RemoteControlMode spyTestClass = spy(testClass);
        doNothing().when(spyTestClass).launchTcpListener();

        AgvOutputsRecord testOutputRecord = new AgvOutputsRecord();

        testOutputRecord = spyTestClass.run(testOutputRecord, new PositionScannerResponseRecord(), new MotorControllerRecord(), new TerminalReader());

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setDirection(Constants.FORWARDS_STRING);
        compareRecord.setNominalVelocity(55);
        compareRecord.setNominalAccel(45);

        Assertions.assertEquals(compareRecord,testOutputRecord);
    }
}

