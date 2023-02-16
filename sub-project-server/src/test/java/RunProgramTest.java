import com.mochilafulfillment.server.Agv;
import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.api.laptop.TerminalReader;
import com.mochilafulfillment.server.communications.tcp_socket.TcpServer;
import com.mochilafulfillment.server.controls_logic.Rotation;
import com.mochilafulfillment.server.controls_logic.dtos.RotationRecord;
import com.mochilafulfillment.server.dtos.AgvOutputsRecord;
import com.mochilafulfillment.server.modes.AutomaticMode;
import com.mochilafulfillment.server.modes.Mode;
import com.mochilafulfillment.server.modes.RemoteControlMode;
import com.mochilafulfillment.server.motor_controller.IO.MotorControllerDigitalInputs;
import com.mochilafulfillment.server.motor_controller.IO.MotorControllerMotorOutputs;
import com.mochilafulfillment.server.motor_controller.MotorController;
import com.mochilafulfillment.server.motor_controller.MotorControllerConstants;
import com.mochilafulfillment.server.motor_controller.dtos.MotorControllerRecord;
import com.mochilafulfillment.server.position_scanner.PositionScanner;
import com.mochilafulfillment.server.position_scanner.dtos.PositionScannerResponseRecord;
import com.mochilafulfillment.server.communications.serial_port.PortHandler;
import com.mochilafulfillment.shared.SharedConstants;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RunProgramTest {

    ReentrantLock motorControllerLock;

    @BeforeEach
    public void init(){
        motorControllerLock = new ReentrantLock();
    }

    @Test
    public void terminalReaderTerminateBeforeStartingModeTest() throws SerialPortException, SerialPortTimeoutException, InterruptedException {
        ByteArrayInputStream in = new ByteArrayInputStream((System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + "c" + System.lineSeparator()).getBytes());
        System.setIn(in);

        MotorControllerRecord motorControllerRecord = new MotorControllerRecord();
        PositionScannerResponseRecord positionScannerTestRecord = new PositionScannerResponseRecord();

        MotorController testMotorController = new MotorController("Test Port", motorControllerRecord, motorControllerLock);
        PositionScanner testPositionScanner = new PositionScanner("Test Port", positionScannerTestRecord);
        PositionScanner spyPositionScanner = spy(testPositionScanner);
        doReturn(new int[]{}).when(spyPositionScanner).receiveResponse();
        doReturn("Test").when(spyPositionScanner).processResponse(any());

        SerialPort mockedPort = mock(SerialPort.class);
        when(mockedPort.openPort()).thenReturn(true);
        PortHandler portHandler = new PortHandler(mockedPort);

        MotorControllerRecord testMotorControllerRecord = new MotorControllerRecord();

        MotorControllerDigitalInputs digitalInputs = new MotorControllerDigitalInputs(portHandler);
        MotorControllerDigitalInputs spyDigitalInputs = spy(digitalInputs);
        doReturn(testMotorControllerRecord).when(spyDigitalInputs).check(any());

        MotorControllerMotorOutputs testMotorOutputs = new MotorControllerMotorOutputs(portHandler);
        MotorControllerMotorOutputs spyMotorOutputs = spy(testMotorOutputs);
        doNothing().when(spyMotorOutputs).write(any(),any());

        testMotorController.setPortHandler(portHandler);
        testMotorController.setDigitalInputs(spyDigitalInputs);
        testMotorController.setMotorOutputs(spyMotorOutputs);

        spyPositionScanner.setPortHandler(portHandler);

        RunProgram runProgram = new RunProgram();

        runProgram.setMotorController(testMotorController);
        runProgram.setPositionScanner(spyPositionScanner);

        ScheduledExecutorService testExecutor = Executors.newScheduledThreadPool(1);
        Future future = testExecutor.submit(runProgram);
        Thread.sleep(SharedConstants.TEST_RUNTIME);
        future.cancel(true);

        MotorControllerRecord postTestMotorControllerRecord = runProgram.getControllerRecord();

        Assertions.assertEquals(postTestMotorControllerRecord.getMotor1Velocity(), 0);
        Assertions.assertEquals(postTestMotorControllerRecord.getMotor2Velocity(), 0);
        Assertions.assertEquals(postTestMotorControllerRecord.getMotor1Sign(), Constants.MOTOR_POSITIVE);
        Assertions.assertEquals(postTestMotorControllerRecord.getMotor2Sign(), Constants.MOTOR_POSITIVE);
        Assertions.assertEquals(postTestMotorControllerRecord.getLiftType(), Constants.STOP_VERTICAL_CONSTANT);
        Assertions.assertEquals(postTestMotorControllerRecord.getNominalAccel(), Constants.STOPPING_ACCELERATION);
    }

    @Test
    public void terminalReaderTerminateAfterStartingModeTest() throws SerialPortException, SerialPortTimeoutException, InterruptedException {
        ByteArrayInputStream in = new ByteArrayInputStream(("2" + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + "c" + System.lineSeparator() ).getBytes());
        System.setIn(in);

        MotorControllerRecord motorControllerRecord = new MotorControllerRecord();
        PositionScannerResponseRecord positionScannerTestRecord = new PositionScannerResponseRecord();

        MotorController testMotorController = new MotorController("Test Port", motorControllerRecord, motorControllerLock);
        PositionScanner testPositionScanner = new PositionScanner("Test Port", positionScannerTestRecord);
        PositionScanner spyPositionScanner = spy(testPositionScanner);
        doReturn(new int[]{}).when(spyPositionScanner).receiveResponse();
        doReturn("Test").when(spyPositionScanner).processResponse(any());

        SerialPort mockedPort = mock(SerialPort.class);
        when(mockedPort.openPort()).thenReturn(true);
        PortHandler portHandler = new PortHandler(mockedPort);

        MotorControllerRecord testMotorControllerRecord = new MotorControllerRecord();

        MotorControllerDigitalInputs digitalInputs = new MotorControllerDigitalInputs(portHandler);
        MotorControllerDigitalInputs spyDigitalInputs = spy(digitalInputs);
        doReturn(testMotorControllerRecord).when(spyDigitalInputs).check(any());

        MotorControllerMotorOutputs testMotorOutputs = new MotorControllerMotorOutputs(portHandler);
        MotorControllerMotorOutputs spyMotorOutputs = spy(testMotorOutputs);
        doNothing().when(spyMotorOutputs).write(any(),any());

        testMotorController.setPortHandler(portHandler);
        testMotorController.setDigitalInputs(spyDigitalInputs);
        testMotorController.setMotorOutputs(spyMotorOutputs);

        spyPositionScanner.setPortHandler(portHandler);

        RunProgram runProgram = spy(new RunProgram());

        runProgram.setMotorController(testMotorController);
        runProgram.setPositionScanner(spyPositionScanner);


        ScheduledExecutorService testExecutor = Executors.newScheduledThreadPool(1);
        Future future = testExecutor.submit(runProgram);
        Thread.sleep(SharedConstants.TEST_RUNTIME);
        future.cancel(true);

        MotorControllerRecord postTestMotorControllerRecord = runProgram.getControllerRecord();

        Assertions.assertEquals(postTestMotorControllerRecord.getLiftType(), Constants.STOP_VERTICAL_CONSTANT);
        Assertions.assertEquals(postTestMotorControllerRecord.getMotor1Velocity(), 0);
        Assertions.assertEquals(postTestMotorControllerRecord.getMotor2Velocity(), 0);
        Assertions.assertEquals(postTestMotorControllerRecord.getNominalAccel(), Constants.STOPPING_ACCELERATION);
        Assertions.assertEquals(postTestMotorControllerRecord.getMotor1Sign(), Constants.MOTOR_POSITIVE);
        Assertions.assertEquals(postTestMotorControllerRecord.getMotor2Sign(), Constants.MOTOR_POSITIVE);
    }

    @Test
    public void terminalReaderChangeModeToAutoTest() throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(("1" + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + "2" + System.lineSeparator() + System.lineSeparator() +"c" + System.lineSeparator()).getBytes());
        System.setIn(in);

        MotorController mockMotorController = mock(MotorController.class);
        PositionScanner mockPositionScanner = mock(PositionScanner.class);

        TerminalReader testTerminalReader = new TerminalReader();

        Agv testAgv = new Agv(testTerminalReader, new MotorControllerRecord(), new PositionScannerResponseRecord(), motorControllerLock);
        Agv spyAgv = spy(testAgv);
        RemoteControlMode testRemoteMode = new RemoteControlMode(SharedConstants.TCP_PORT);
        RemoteControlMode spyRemoteMode = spy(testRemoteMode);
        TcpServer mockTcpServer = mock(TcpServer.class);
        spyRemoteMode.setTcpServer(mockTcpServer);
        doNothing().when(spyRemoteMode).launchTcpListener();
        doReturn(spyRemoteMode).doCallRealMethod().when(spyAgv).initialOperationMode();

        RunProgram runProgram = spy(new RunProgram());

        runProgram.setMotorController(mockMotorController);
        runProgram.setPositionScanner(mockPositionScanner);
        runProgram.setAgv(spyAgv);
        runProgram.setTerminalReader(testTerminalReader);

        ScheduledExecutorService testExecutor = Executors.newScheduledThreadPool(1);
        Future future = testExecutor.submit(runProgram);
        Thread.sleep(SharedConstants.TEST_RUNTIME);
        future.cancel(true);

        Mode postTestModeObject = runProgram.getAgv().getModeObject();

        Assertions.assertTrue(postTestModeObject instanceof AutomaticMode);

    }

    @Test
    public void terminalReaderChangeModeToRemoteTest() throws SerialPortException, InterruptedException, SerialPortTimeoutException {
        ByteArrayInputStream in = new ByteArrayInputStream(("2" + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + "1" + System.lineSeparator() + "c" + System.lineSeparator()).getBytes());
        System.setIn(in);

        MotorControllerRecord motorControllerRecord = new MotorControllerRecord();
        PositionScannerResponseRecord positionScannerTestRecord = new PositionScannerResponseRecord();

        MotorController testMotorController = new MotorController("Test Port", motorControllerRecord, motorControllerLock);
        PositionScanner testPositionScanner = new PositionScanner("Test Port", positionScannerTestRecord);
        PositionScanner spyPositionScanner = spy(testPositionScanner);
        doReturn(new int[]{}).when(spyPositionScanner).receiveResponse();
        doNothing().when(spyPositionScanner).makeRequest();
        doReturn("Test").when(spyPositionScanner).processResponse(any());

        SerialPort mockedPort = mock(SerialPort.class);
        when(mockedPort.openPort()).thenReturn(true);
        PortHandler portHandler = new PortHandler(mockedPort);

        MotorControllerRecord testMotorControllerRecord = new MotorControllerRecord();

        MotorControllerDigitalInputs digitalInputs = new MotorControllerDigitalInputs(portHandler);
        MotorControllerDigitalInputs spyDigitalInputs = spy(digitalInputs);
        doReturn(testMotorControllerRecord).when(spyDigitalInputs).check(any());

        MotorControllerMotorOutputs testMotorOutputs = new MotorControllerMotorOutputs(portHandler);
        MotorControllerMotorOutputs spyMotorOutputs = spy(testMotorOutputs);
        doNothing().when(spyMotorOutputs).write(any(),any());
        doReturn(true).when(spyMotorOutputs).initialize();


        testMotorController.setPortHandler(portHandler);
        testMotorController.setDigitalInputs(spyDigitalInputs);
        testMotorController.setMotorOutputs(spyMotorOutputs);

        spyPositionScanner.setPortHandler(portHandler);

        RunProgram runProgram = spy(new RunProgram());

        runProgram.setMotorController(testMotorController);
        runProgram.setPositionScanner(spyPositionScanner);

        ScheduledExecutorService testExecutor = Executors.newScheduledThreadPool(1);
        Future future = testExecutor.submit(runProgram);
        Thread.sleep(SharedConstants.TEST_RUNTIME);
        future.cancel(true);

        Mode postTestModeObject = runProgram.getAgv().getModeObject();

        Assertions.assertTrue(postTestModeObject instanceof RemoteControlMode);
    }

    @Test //need to use testpath in Automatic mode
    public void straightLineRollOnStopTagTest() throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(("2" + System.lineSeparator()).getBytes());
        System.setIn(in);

        AutomaticMode testMode = new AutomaticMode(Constants.TEST_FILE_NAME, Constants.PID_CONTROL);
        int testTagID = testMode.getPickPath().peekNext().getTagId();

        MotorControllerRecord motorControllerTestRecord = new MotorControllerRecord();
        motorControllerTestRecord.setDirection(Constants.FORWARDS_STRING);
        motorControllerTestRecord.setSafetyScannerMode(Constants.REGULAR_SCAN);
        motorControllerTestRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);
        motorControllerTestRecord.setMotor1Sign(Constants.MOTOR_POSITIVE);
        motorControllerTestRecord.setMotor2Sign(Constants.MOTOR_NEGATIVE);
        motorControllerTestRecord.setMotor1Velocity(Constants.STANDARD_VELOCITY);
        motorControllerTestRecord.setMotor2Velocity(Constants.STANDARD_VELOCITY);
        motorControllerTestRecord.setBottomLiftSensor(true);
        motorControllerTestRecord.setNewMotorControllerRecord(true);
        MotorControllerRecord spyMotorControllerTestRecord = spy(motorControllerTestRecord);
        when(spyMotorControllerTestRecord.setStopped()).thenReturn(spyMotorControllerTestRecord);

        PositionScannerResponseRecord positionScannerTestRecord = new PositionScannerResponseRecord();
        positionScannerTestRecord.setTagId(testTagID);
        positionScannerTestRecord.setXPosition(-20);
        positionScannerTestRecord.setYPosition(0);
        positionScannerTestRecord.setColumns(40);
        positionScannerTestRecord.setRows(30);

        MotorController testMotorController = new MotorController("Test Port", spyMotorControllerTestRecord, motorControllerLock);
        PositionScanner testPositionScanner = new PositionScanner("Test Port", positionScannerTestRecord);
        PositionScanner spyPositionScanner = spy(testPositionScanner);
        doReturn(new int[]{}).when(spyPositionScanner).receiveResponse();
        doReturn("Test").when(spyPositionScanner).processResponse(any());

        SerialPort mockedPort = mock(SerialPort.class);
        doReturn(true).when(mockedPort).openPort();
        doReturn(true).when(mockedPort).writeBytes(any());
        doReturn(new int[] {MotorControllerConstants.EXPECTED_RESPONSE_FROM_COMMAND}).when(mockedPort).readIntArray(1, Constants.MOTOR_CONTROLLER_TIMEOUT);

        PortHandler portHandler = new PortHandler(mockedPort);

        MotorControllerDigitalInputs digitalInputs = new MotorControllerDigitalInputs(portHandler);
        MotorControllerDigitalInputs spyDigitalInputs = spy(digitalInputs);
        doReturn(spyMotorControllerTestRecord).when(spyDigitalInputs).check(any());

        testMotorController.setPortHandler(portHandler);
        testMotorController.setDigitalInputs(spyDigitalInputs);
        spyPositionScanner.setPortHandler(portHandler);

        MotorControllerMotorOutputs mockedMotorOutputs = mock(MotorControllerMotorOutputs.class);
        doReturn(true).when(mockedMotorOutputs).initialize();

        testMotorController.setPortHandler(portHandler);
        testMotorController.setDigitalInputs(spyDigitalInputs);
        spyPositionScanner.setPortHandler(portHandler);
        testMotorController.setMotorOutputs(mockedMotorOutputs);
        spyPositionScanner.setPortHandler(portHandler);

        TerminalReader testTerminalReader = new TerminalReader();

        Agv testAgv = new Agv(testTerminalReader, spyMotorControllerTestRecord, positionScannerTestRecord, motorControllerLock);
        Agv spyAgv = spy(testAgv);
        spyAgv.setFileName(Constants.TEST_FILE_NAME);
        doReturn(testMode).when(spyAgv).initialOperationMode();

        RunProgram runProgram = new RunProgram();
        runProgram.setAgv(spyAgv);

        runProgram.setControllerRecord(spyMotorControllerTestRecord);
        runProgram.setMotorController(testMotorController);
        runProgram.setPositionScanner(spyPositionScanner);
        runProgram.setTerminalReader(testTerminalReader);

        ScheduledExecutorService testExecutor = Executors.newScheduledThreadPool(1);
        Future future = testExecutor.submit(runProgram);
        Thread.sleep(SharedConstants.TEST_RUNTIME);
        future.cancel(true);

        String direction = runProgram.getMotorController().getControllerRecord().getDirection();
        double motor1Velocity = runProgram.getMotorController().getControllerRecord().getMotor1Velocity();
        double motor2Velocity = runProgram.getMotorController().getControllerRecord().getMotor2Velocity();
        double acceleration = runProgram.getMotorController().getControllerRecord().getNominalAccel();

        Assertions.assertEquals(direction, Constants.FORWARDS_STRING);
        Assertions.assertEquals(motor1Velocity, Constants.ON_STOP_TAG_VELOCITY);
        Assertions.assertEquals(motor2Velocity, Constants.ON_STOP_TAG_VELOCITY);
        Assertions.assertEquals(acceleration, Constants.ON_STOP_TAG_ACCEL);
    }


    @Test
    public void stopOnTagFinishRotateBeginTest() throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(("2" + System.lineSeparator()).getBytes());
        System.setIn(in);

        Rotation testRotation = new Rotation();
        testRotation.setRotationType(Constants.NO_CONTROL_ROTATION);

        AutomaticMode testMode = new AutomaticMode(Constants.TEST_FILE_NAME, Constants.PID_CONTROL);
        int testTagID = testMode.getPickPath().peekNext().getTagId();
        testMode.setRotation(testRotation);

        MotorControllerRecord motorControllerTestRecord = new MotorControllerRecord();
        motorControllerTestRecord.setDirection(Constants.STOP_STRING);
        motorControllerTestRecord.setSafetyScannerMode(Constants.REGULAR_SCAN);
        motorControllerTestRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);
        motorControllerTestRecord.setMotor1Sign(Constants.MOTOR_POSITIVE);
        motorControllerTestRecord.setMotor2Sign(Constants.MOTOR_POSITIVE);
        motorControllerTestRecord.setMotor1Velocity(0);
        motorControllerTestRecord.setMotor2Velocity(0);
        motorControllerTestRecord.setBottomLiftSensor(true);
        motorControllerTestRecord.setNewMotorControllerRecord(true);
        MotorControllerRecord spyMotorControllerTestRecord = spy(motorControllerTestRecord);
        when(spyMotorControllerTestRecord.setStopped()).thenReturn(spyMotorControllerTestRecord);

        PositionScannerResponseRecord positionScannerTestRecord = new PositionScannerResponseRecord();
        positionScannerTestRecord.setTagId(testTagID);
        positionScannerTestRecord.setXPosition(5);
        positionScannerTestRecord.setYPosition(0);
        positionScannerTestRecord.setColumns(40);
        positionScannerTestRecord.setRows(30);

        MotorController testMotorController = new MotorController("Test Port", spyMotorControllerTestRecord, motorControllerLock);
        PositionScanner testPositionScanner = new PositionScanner("Test Port", positionScannerTestRecord);
        PositionScanner spyPositionScanner = spy(testPositionScanner);
        doReturn(new int[]{}).when(spyPositionScanner).receiveResponse();
        doReturn("Test").when(spyPositionScanner).processResponse(any());

        SerialPort mockedPort = mock(SerialPort.class);
        doReturn(true).when(mockedPort).openPort();
        doReturn(true).when(mockedPort).writeBytes(any());
        doReturn(new int[] {MotorControllerConstants.EXPECTED_RESPONSE_FROM_COMMAND}).when(mockedPort).readIntArray(1, Constants.MOTOR_CONTROLLER_TIMEOUT);

        PortHandler portHandler = new PortHandler(mockedPort);

        MotorControllerDigitalInputs digitalInputs = new MotorControllerDigitalInputs(portHandler);
        MotorControllerDigitalInputs spyDigitalInputs = spy(digitalInputs);
        doReturn(spyMotorControllerTestRecord).when(spyDigitalInputs).check(any());

        testMotorController.setPortHandler(portHandler);
        testMotorController.setDigitalInputs(spyDigitalInputs);
        spyPositionScanner.setPortHandler(portHandler);

        MotorControllerMotorOutputs mockedMotorOutputs = mock(MotorControllerMotorOutputs.class);
        doReturn(true).when(mockedMotorOutputs).initialize();

        testMotorController.setPortHandler(portHandler);
        testMotorController.setDigitalInputs(spyDigitalInputs);
        spyPositionScanner.setPortHandler(portHandler);
        testMotorController.setMotorOutputs(mockedMotorOutputs);
        spyPositionScanner.setPortHandler(portHandler);

        TerminalReader testTerminalReader = new TerminalReader();

        AgvOutputsRecord testOutputs = new AgvOutputsRecord();
        testOutputs.setAgvStopped(true);
        AgvOutputsRecord spyOutputs = spy(testOutputs);
        doReturn(true).when(spyOutputs).getTagIsFinished();


        Agv testAgv = new Agv(testTerminalReader, spyMotorControllerTestRecord, positionScannerTestRecord, motorControllerLock);
        testAgv.setFileName(Constants.TEST_FILE_NAME);
        testAgv.setAgvOutputs(spyOutputs);
        testAgv.setModeObject(testMode);

        RunProgram runProgram = new RunProgram();
        runProgram.setAgv(testAgv);

        runProgram.setControllerRecord(spyMotorControllerTestRecord);
        runProgram.setMotorController(testMotorController);
        runProgram.setPositionScanner(spyPositionScanner);
        runProgram.setTerminalReader(testTerminalReader);

        ScheduledExecutorService testExecutor = Executors.newScheduledThreadPool(1);
        Future future = testExecutor.submit(runProgram);
        Thread.sleep(SharedConstants.TEST_RUNTIME);
        future.cancel(true);

        String direction = runProgram.getMotorController().getControllerRecord().getDirection();
        double motor1Velocity = runProgram.getMotorController().getControllerRecord().getMotor1Velocity();
        double motor2Velocity = runProgram.getMotorController().getControllerRecord().getMotor2Velocity();
        double acceleration = runProgram.getMotorController().getControllerRecord().getNominalAccel();
        int scannerMode = runProgram.getMotorController().getControllerRecord().getSafetyScannerMode();
        String motor1Sign = runProgram.getMotorController().getControllerRecord().getMotor1Sign();
        String motor2Sign = runProgram.getMotorController().getControllerRecord().getMotor2Sign();

        Assertions.assertEquals(direction, Constants.CW_STRING);
        Assertions.assertEquals(motor1Velocity, Constants.ROTATE_SPEED);
        Assertions.assertEquals(motor2Velocity, Constants.ROTATE_SPEED);
        Assertions.assertEquals(acceleration, Constants.ROTATE_ACCEL);
        Assertions.assertEquals(scannerMode, Constants.REGULAR_SCAN);
        Assertions.assertEquals(motor1Sign, Constants.MOTOR_NEGATIVE);
        Assertions.assertEquals(motor2Sign, Constants.MOTOR_POSITIVE);
    }

    @Test
    public void rotateEndsTest() throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(("2" + System.lineSeparator()).getBytes());
        System.setIn(in);

        RotationRecord rotationRecord = new RotationRecord();
        RotationRecord spyRotationRecord = spy(rotationRecord);
        doReturn(true).when(spyRotationRecord).isStartedRotation();
        Rotation testRotation = new Rotation();
        testRotation.setRotationRecord(spyRotationRecord);

        AutomaticMode testMode = new AutomaticMode(Constants.TEST_FILE_NAME, Constants.PID_CONTROL);
        int testTagID = testMode.getPickPath().peekNext().getTagId();
        testMode.setRotation(testRotation);

        MotorControllerRecord motorControllerTestRecord = new MotorControllerRecord();
        motorControllerTestRecord.setDirection(Constants.CCW_STRING);
        motorControllerTestRecord.setSafetyScannerMode(Constants.REGULAR_SCAN);
        motorControllerTestRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);
        motorControllerTestRecord.setMotor1Sign(Constants.MOTOR_NEGATIVE);
        motorControllerTestRecord.setMotor2Sign(Constants.MOTOR_POSITIVE);
        motorControllerTestRecord.setMotor1Velocity(Constants.ROTATE_SPEED);
        motorControllerTestRecord.setMotor2Velocity(Constants.ROTATE_SPEED);
        motorControllerTestRecord.setNominalAccel(Constants.ROTATE_ACCEL);
        motorControllerTestRecord.setBottomLiftSensor(true);
        motorControllerTestRecord.setNewMotorControllerRecord(true);
        MotorControllerRecord spyMotorControllerTestRecord = spy(motorControllerTestRecord);
        when(spyMotorControllerTestRecord.setStopped()).thenReturn(spyMotorControllerTestRecord);

        PositionScannerResponseRecord positionScannerTestRecord = new PositionScannerResponseRecord();
        positionScannerTestRecord.setTagId(testTagID);
        positionScannerTestRecord.setXPosition(5);
        positionScannerTestRecord.setYPosition(0);
        positionScannerTestRecord.setColumns(40);
        positionScannerTestRecord.setRows(30);
        positionScannerTestRecord.setTagAngle(90);

        MotorController testMotorController = new MotorController("Test Port", spyMotorControllerTestRecord, motorControllerLock);
        PositionScanner testPositionScanner = new PositionScanner("Test Port", positionScannerTestRecord);
        PositionScanner spyPositionScanner = spy(testPositionScanner);
        doReturn(new int[]{}).when(spyPositionScanner).receiveResponse();
        doReturn("Test").when(spyPositionScanner).processResponse(any());

        SerialPort mockedPort = mock(SerialPort.class);
        doReturn(true).when(mockedPort).openPort();
        doReturn(true).when(mockedPort).writeBytes(any());
        doReturn(new int[] {MotorControllerConstants.EXPECTED_RESPONSE_FROM_COMMAND}).when(mockedPort).readIntArray(1, Constants.MOTOR_CONTROLLER_TIMEOUT);

        PortHandler portHandler = new PortHandler(mockedPort);

        MotorControllerDigitalInputs digitalInputs = new MotorControllerDigitalInputs(portHandler);
        MotorControllerDigitalInputs spyDigitalInputs = spy(digitalInputs);
        doReturn(spyMotorControllerTestRecord).when(spyDigitalInputs).check(any());

        MotorControllerMotorOutputs mockedMotorOutputs = mock(MotorControllerMotorOutputs.class);
        doReturn(true).when(mockedMotorOutputs).initialize();

        testMotorController.setPortHandler(portHandler);
        testMotorController.setDigitalInputs(spyDigitalInputs);
        spyPositionScanner.setPortHandler(portHandler);
        testMotorController.setMotorOutputs(mockedMotorOutputs);
        spyPositionScanner.setPortHandler(portHandler);

        TerminalReader testTerminalReader = new TerminalReader();

        AgvOutputsRecord testOutputs = new AgvOutputsRecord();
        testOutputs.setAgvStopped(true);
        AgvOutputsRecord spyOutputs = spy(testOutputs);
        doReturn(true).when(spyOutputs).getTagIsFinished();

        Agv testAgv = new Agv(testTerminalReader, spyMotorControllerTestRecord, positionScannerTestRecord, motorControllerLock);
        testAgv.setFileName(Constants.TEST_FILE_NAME);
        testAgv.setAgvOutputs(spyOutputs);
        Agv spyAgv = spy(testAgv);
        doReturn(testMode).when(spyAgv).initialOperationMode();

        RunProgram runProgram = new RunProgram();
        runProgram.setAgv(spyAgv);

        runProgram.setControllerRecord(spyMotorControllerTestRecord);
        runProgram.setMotorController(testMotorController);
        runProgram.setPositionScanner(spyPositionScanner);
        runProgram.setTerminalReader(testTerminalReader);

        ScheduledExecutorService testExecutor = Executors.newScheduledThreadPool(1);
        Future future = testExecutor.submit(runProgram);
        Thread.sleep(SharedConstants.TEST_RUNTIME);
        future.cancel(true);

        String direction = runProgram.getMotorController().getControllerRecord().getDirection();
        double motor1Velocity = runProgram.getMotorController().getControllerRecord().getMotor1Velocity();
        double motor2Velocity = runProgram.getMotorController().getControllerRecord().getMotor2Velocity();
        double acceleration = runProgram.getMotorController().getControllerRecord().getNominalAccel();
        int scannerMode = runProgram.getMotorController().getControllerRecord().getSafetyScannerMode();
        String motor1Sign = runProgram.getMotorController().getControllerRecord().getMotor1Sign();
        String motor2Sign = runProgram.getMotorController().getControllerRecord().getMotor2Sign();

        Assertions.assertEquals(direction, Constants.STOP_STRING);
        Assertions.assertEquals(motor1Velocity, 0);
        Assertions.assertEquals(motor2Velocity, 0);
        Assertions.assertEquals(acceleration, Constants.STOPPING_ACCELERATION);
        Assertions.assertEquals(scannerMode, Constants.PICKING_SCAN);
        Assertions.assertEquals(motor1Sign, Constants.MOTOR_POSITIVE);
        Assertions.assertEquals(motor2Sign, Constants.MOTOR_POSITIVE);
    }


    @Test
    public void liftBeginsTest() throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(("2" + System.lineSeparator()).getBytes());
        System.setIn(in);

        RotationRecord rotationRecord = new RotationRecord();
        RotationRecord spyRotationRecord = spy(rotationRecord);
        doReturn(true).when(spyRotationRecord).isStartedRotation();
        Rotation testRotation = new Rotation();
        testRotation.setRotationRecord(spyRotationRecord);

        AutomaticMode testMode = new AutomaticMode(Constants.TEST_FILE_NAME, Constants.PID_CONTROL);
        int testTagID = testMode.getPickPath().peekNext().getTagId();
        testMode.setRotation(testRotation);

        MotorControllerRecord motorControllerTestRecord = new MotorControllerRecord();
        motorControllerTestRecord.setDirection(Constants.STOP_STRING);
        motorControllerTestRecord.setSafetyScannerMode(Constants.PICKING_SCAN);
        motorControllerTestRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);
        motorControllerTestRecord.setMotor1Sign(Constants.MOTOR_POSITIVE);
        motorControllerTestRecord.setMotor2Sign(Constants.MOTOR_POSITIVE);
        motorControllerTestRecord.setMotor1Velocity(0);
        motorControllerTestRecord.setMotor2Velocity(0);
        motorControllerTestRecord.setTopLiftSensor(true);
        motorControllerTestRecord.setNewMotorControllerRecord(true);

        PositionScannerResponseRecord positionScannerTestRecord = new PositionScannerResponseRecord();
        positionScannerTestRecord.setTagId(testTagID);
        positionScannerTestRecord.setXPosition(5);
        positionScannerTestRecord.setYPosition(0);
        positionScannerTestRecord.setColumns(40);
        positionScannerTestRecord.setRows(30);
        positionScannerTestRecord.setTagAngle(90);

        MotorController testMotorController = new MotorController("Test Port", motorControllerTestRecord, motorControllerLock);
        PositionScanner testPositionScanner = new PositionScanner("Test Port", positionScannerTestRecord);
        PositionScanner spyPositionScanner = spy(testPositionScanner);
        doReturn(new int[]{}).when(spyPositionScanner).receiveResponse();
        doReturn("Test").when(spyPositionScanner).processResponse(any());

        SerialPort mockedPort = mock(SerialPort.class);
        doReturn(true).when(mockedPort).openPort();
        doReturn(true).when(mockedPort).writeBytes(any());
        doReturn(new int[] {MotorControllerConstants.EXPECTED_RESPONSE_FROM_COMMAND}).when(mockedPort).readIntArray(1, Constants.MOTOR_CONTROLLER_TIMEOUT);

        PortHandler portHandler = new PortHandler(mockedPort);

        MotorControllerDigitalInputs digitalInputs = new MotorControllerDigitalInputs(portHandler);
        MotorControllerDigitalInputs spyDigitalInputs = spy(digitalInputs);
        doReturn(motorControllerTestRecord).when(spyDigitalInputs).check(any());

        MotorControllerMotorOutputs mockedMotorOutputs = mock(MotorControllerMotorOutputs.class);
        doReturn(true).when(mockedMotorOutputs).initialize();

        testMotorController.setPortHandler(portHandler);
        testMotorController.setDigitalInputs(spyDigitalInputs);
        spyPositionScanner.setPortHandler(portHandler);
        testMotorController.setMotorOutputs(mockedMotorOutputs);
        spyPositionScanner.setPortHandler(portHandler);

        TerminalReader testTerminalReader = new TerminalReader();

        AgvOutputsRecord testOutputs = new AgvOutputsRecord();
        testOutputs.setAgvStopped(true);
        AgvOutputsRecord spyOutputs = spy(testOutputs);
        doReturn(true).when(spyOutputs).getTagIsFinished();

        Agv testAgv = new Agv(testTerminalReader, motorControllerTestRecord, positionScannerTestRecord, motorControllerLock);
        testAgv.setFileName(Constants.TEST_FILE_NAME);
        testAgv.setAgvOutputs(spyOutputs);
        Agv spyAgv = spy(testAgv);
        doReturn(testMode).when(spyAgv).initialOperationMode();

        RunProgram runProgram = new RunProgram();
        runProgram.setAgv(spyAgv);

        runProgram.setControllerRecord(motorControllerTestRecord);
        runProgram.setMotorController(testMotorController);
        runProgram.setPositionScanner(spyPositionScanner);
        runProgram.setTerminalReader(testTerminalReader);

        ScheduledExecutorService testExecutor = Executors.newScheduledThreadPool(1);
        Future future = testExecutor.submit(runProgram);
        Thread.sleep(SharedConstants.TEST_RUNTIME);
        future.cancel(true);

        String direction = runProgram.getMotorController().getControllerRecord().getDirection();
        double motor1Velocity = runProgram.getMotorController().getControllerRecord().getMotor1Velocity();
        double motor2Velocity = runProgram.getMotorController().getControllerRecord().getMotor2Velocity();
        double acceleration = runProgram.getMotorController().getControllerRecord().getNominalAccel();
        int scannerMode = runProgram.getMotorController().getControllerRecord().getSafetyScannerMode();
        String motor1Sign = runProgram.getMotorController().getControllerRecord().getMotor1Sign();
        String motor2Sign = runProgram.getMotorController().getControllerRecord().getMotor2Sign();
        int liftType = runProgram.getMotorController().getControllerRecord().getLiftType();

        Assertions.assertEquals(direction, Constants.STOP_STRING);
        Assertions.assertEquals(motor1Velocity, 0);
        Assertions.assertEquals(motor2Velocity, 0);
        Assertions.assertEquals(acceleration, Constants.STOPPING_ACCELERATION);
        Assertions.assertEquals(scannerMode, Constants.PICKING_SCAN);
        Assertions.assertEquals(motor1Sign, Constants.MOTOR_POSITIVE);
        Assertions.assertEquals(motor2Sign, Constants.MOTOR_POSITIVE);
        Assertions.assertEquals(liftType, Constants.LIFT_CONSTANT);

    }

    @Test
    public void liftCompleteStartMovingTest() throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(("2" + System.lineSeparator()).getBytes());
        System.setIn(in);

        RotationRecord rotationRecord = new RotationRecord();
        RotationRecord spyRotationRecord = spy(rotationRecord);
        doReturn(true).when(spyRotationRecord).isStartedRotation();
        Rotation testRotation = new Rotation();
        testRotation.setRotationRecord(spyRotationRecord);

        AutomaticMode testMode = new AutomaticMode(Constants.TEST_FILE_NAME, Constants.PID_CONTROL);
        int testTagID = testMode.getPickPath().peekNext().getTagId() - 1;
        testMode.setRotation(testRotation);

        MotorControllerRecord motorControllerTestRecord = new MotorControllerRecord();
        motorControllerTestRecord.setDirection(Constants.STOP_STRING);
        motorControllerTestRecord.setSafetyScannerMode(Constants.PICKING_SCAN);
        motorControllerTestRecord.setMotor1Sign(Constants.MOTOR_POSITIVE);
        motorControllerTestRecord.setMotor2Sign(Constants.MOTOR_POSITIVE);
        motorControllerTestRecord.setMotor1Velocity(0);
        motorControllerTestRecord.setMotor2Velocity(0);
        motorControllerTestRecord.setMiddleLiftSensor(true);
        motorControllerTestRecord.setNewMotorControllerRecord(true);
        MotorControllerRecord spyMotorControllerTestRecord = spy(motorControllerTestRecord);
        when(spyMotorControllerTestRecord.getLiftType()).thenReturn(Constants.STOP_VERTICAL_CONSTANT, Constants.LIFT_CONSTANT).thenCallRealMethod();
        when(spyMotorControllerTestRecord.isTopLiftSensor()).thenReturn(true,  true, true, true, false, false);
        when(spyMotorControllerTestRecord.isMiddleLiftSensor()).thenReturn(false, false, false, false, false, true);
        when(spyMotorControllerTestRecord.setStopped()).thenReturn(spyMotorControllerTestRecord);

        PositionScannerResponseRecord positionScannerTestRecord = new PositionScannerResponseRecord();
        positionScannerTestRecord.setTagId(testTagID);
        positionScannerTestRecord.setXPosition(5);
        positionScannerTestRecord.setYPosition(0);
        positionScannerTestRecord.setColumns(40);
        positionScannerTestRecord.setRows(30);
        positionScannerTestRecord.setTagAngle(90);

        MotorController testMotorController = new MotorController("Test Port", spyMotorControllerTestRecord, motorControllerLock);
        PositionScanner testPositionScanner = new PositionScanner("Test Port", positionScannerTestRecord);
        PositionScanner spyPositionScanner = spy(testPositionScanner);
        doReturn(new int[]{}).when(spyPositionScanner).receiveResponse();
        doReturn("Test").when(spyPositionScanner).processResponse(any());

        SerialPort mockedPort = mock(SerialPort.class);
        doReturn(true).when(mockedPort).openPort();
        doReturn(true).when(mockedPort).writeBytes(any());
        doReturn(new int[] {MotorControllerConstants.EXPECTED_RESPONSE_FROM_COMMAND}).when(mockedPort).readIntArray(1, Constants.MOTOR_CONTROLLER_TIMEOUT);

        PortHandler portHandler = new PortHandler(mockedPort);

        MotorControllerDigitalInputs digitalInputs = new MotorControllerDigitalInputs(portHandler);
        MotorControllerDigitalInputs spyDigitalInputs = spy(digitalInputs);
        doReturn(spyMotorControllerTestRecord).when(spyDigitalInputs).check(any());

        testMotorController.setPortHandler(portHandler);
        testMotorController.setDigitalInputs(spyDigitalInputs);
        spyPositionScanner.setPortHandler(portHandler);

        TerminalReader testTerminalReader = new TerminalReader();

        AgvOutputsRecord testOutputs = new AgvOutputsRecord();
        testOutputs.setAgvStopped(true);
        AgvOutputsRecord spyOutputs = spy(testOutputs);
        when(spyOutputs.getTagIsFinished()).thenReturn(true).thenCallRealMethod();

        Agv testAgv = new Agv(testTerminalReader, spyMotorControllerTestRecord, positionScannerTestRecord, motorControllerLock);
        testAgv.setFileName(Constants.TEST_FILE_NAME);
        testAgv.setAgvOutputs(spyOutputs);
        testAgv.setControllerRecord(spyMotorControllerTestRecord);
        Agv spyAgv = spy(testAgv);
        doReturn(testMode).when(spyAgv).initialOperationMode();

        RunProgram runProgram = new RunProgram();
        runProgram.setAgv(spyAgv);

        runProgram.setControllerRecord(spyMotorControllerTestRecord);
        runProgram.setMotorController(testMotorController);
        runProgram.setPositionScanner(spyPositionScanner);
        runProgram.setTerminalReader(testTerminalReader);

        ScheduledExecutorService testExecutor = Executors.newScheduledThreadPool(1);
        Future future = testExecutor.submit(runProgram);
        Thread.sleep(SharedConstants.TEST_RUNTIME);
        future.cancel(true);

        String direction = runProgram.getMotorController().getControllerRecord().getDirection();
        double motor1Velocity = runProgram.getMotorController().getControllerRecord().getMotor1Velocity();
        double motor2Velocity = runProgram.getMotorController().getControllerRecord().getMotor2Velocity();

        double acceleration = runProgram.getMotorController().getControllerRecord().getNominalAccel();
        int scannerMode = runProgram.getMotorController().getControllerRecord().getSafetyScannerMode();
        String motor1Sign = runProgram.getMotorController().getControllerRecord().getMotor1Sign();
        String motor2Sign = runProgram.getMotorController().getControllerRecord().getMotor2Sign();
        int liftType = runProgram.getMotorController().getControllerRecord().getLiftType();

        Assertions.assertEquals(direction, Constants.FORWARDS_STRING);
        Assertions.assertEquals(motor1Velocity, Constants.STANDARD_VELOCITY);
        Assertions.assertEquals(motor2Velocity, Constants.STANDARD_VELOCITY);
        Assertions.assertEquals(acceleration, Constants.STANDARD_ACCELERATION);
        Assertions.assertEquals(scannerMode, Constants.REGULAR_SCAN);
        Assertions.assertEquals(motor1Sign, Constants.MOTOR_POSITIVE);
        Assertions.assertEquals(motor2Sign, Constants.MOTOR_POSITIVE);
        Assertions.assertEquals(liftType, Constants.STOP_VERTICAL_CONSTANT);


    }

    @Test
    public void straightLineStartTest() throws SerialPortException, SerialPortTimeoutException, InterruptedException, IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(("2" + System.lineSeparator()).getBytes());
        System.setIn(in);

        MotorControllerRecord motorControllerRecord = new MotorControllerRecord();

        PositionScannerResponseRecord positionScannerTestRecord = new PositionScannerResponseRecord();
        positionScannerTestRecord.setTagId(0);
        positionScannerTestRecord.setOnTape(true);

        MotorController testMotorController = new MotorController("Test Port", motorControllerRecord, motorControllerLock);
        PositionScanner testPositionScanner = new PositionScanner("Test Port", positionScannerTestRecord);
        PositionScanner spyPositionScanner = spy(testPositionScanner);
        doReturn(new int[]{}).when(spyPositionScanner).receiveResponse();
        doReturn("Test").when(spyPositionScanner).processResponse(any());

        SerialPort mockedPort = mock(SerialPort.class);
        doReturn(true).when(mockedPort).openPort();
        doReturn(true).when(mockedPort).writeBytes(any());
        doReturn(new int[] {MotorControllerConstants.EXPECTED_RESPONSE_FROM_COMMAND}).when(mockedPort).readIntArray(1, Constants.MOTOR_CONTROLLER_TIMEOUT);

        PortHandler portHandler = new PortHandler(mockedPort);

        MotorControllerDigitalInputs digitalInputs = new MotorControllerDigitalInputs(portHandler);
        MotorControllerDigitalInputs spyDigitalInputs = spy(digitalInputs);
        doReturn(motorControllerRecord).when(spyDigitalInputs).check(any());

        MotorControllerMotorOutputs mockedMotorOutputs = mock(MotorControllerMotorOutputs.class);
        doReturn(true).when(mockedMotorOutputs).initialize();

        testMotorController.setPortHandler(portHandler);
        testMotorController.setDigitalInputs(spyDigitalInputs);
        spyPositionScanner.setPortHandler(portHandler);
        testMotorController.setMotorOutputs(mockedMotorOutputs);
        spyPositionScanner.setPortHandler(portHandler);

        TerminalReader testTerminalReader = new TerminalReader();

        AutomaticMode testMode = new AutomaticMode(Constants.TEST_FILE_NAME, Constants.PID_CONTROL);

        Agv testAgv = new Agv(testTerminalReader, motorControllerRecord, positionScannerTestRecord, motorControllerLock);
        Agv spyAgv = spy(testAgv);
        spyAgv.setFileName(Constants.TEST_FILE_NAME);
        doReturn(testMode).when(spyAgv).initialOperationMode();

        RunProgram runProgram = new RunProgram();
        runProgram.setAgv(spyAgv);

        runProgram.setControllerRecord(motorControllerRecord);
        runProgram.setMotorController(testMotorController);
        runProgram.setPositionScanner(spyPositionScanner);
        runProgram.setTerminalReader(testTerminalReader);

        ScheduledExecutorService testExecutor = Executors.newScheduledThreadPool(1);
        Future future = testExecutor.submit(runProgram);
        Thread.sleep(SharedConstants.TEST_RUNTIME);
        future.cancel(true);

        String direction = runProgram.getMotorController().getControllerRecord().getDirection();
        double motor1Velocity = runProgram.getMotorController().getControllerRecord().getMotor1Velocity();
        double motor2Velocity = runProgram.getMotorController().getControllerRecord().getMotor2Velocity();
        double acceleration = runProgram.getMotorController().getControllerRecord().getNominalAccel();
        int scannerMode = runProgram.getMotorController().getControllerRecord().getSafetyScannerMode();
        String motor1Sign = runProgram.getMotorController().getControllerRecord().getMotor1Sign();
        String motor2Sign = runProgram.getMotorController().getControllerRecord().getMotor2Sign();
        int liftType = runProgram.getMotorController().getControllerRecord().getLiftType();

        Assertions.assertEquals(direction, Constants.FORWARDS_STRING);
        Assertions.assertEquals(motor1Velocity, Constants.STANDARD_VELOCITY);
        Assertions.assertEquals(motor2Velocity, Constants.STANDARD_VELOCITY);
        Assertions.assertEquals(acceleration, Constants.STANDARD_ACCELERATION);
        Assertions.assertEquals(scannerMode, Constants.REGULAR_SCAN);
        Assertions.assertEquals(motor1Sign, Constants.MOTOR_POSITIVE);
        Assertions.assertEquals(motor2Sign, Constants.MOTOR_POSITIVE);
        Assertions.assertEquals(liftType, Constants.STOP_VERTICAL_CONSTANT);
    }


    @Test
    public void terminalReaderExceptionTest() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        MotorControllerRecord motorControllerRecord = new MotorControllerRecord();

        PositionScannerResponseRecord positionScannerTestRecord = new PositionScannerResponseRecord();
        positionScannerTestRecord.setTagId(0);
        positionScannerTestRecord.setOnTape(true);

        MotorController testMotorController = new MotorController("Test Port", motorControllerRecord, motorControllerLock);
        PositionScanner testPositionScanner = new PositionScanner("Test Port", positionScannerTestRecord);
        PositionScanner spyPositionScanner = spy(testPositionScanner);
        doReturn(new int[]{}).when(spyPositionScanner).receiveResponse();
        doReturn("Test").when(spyPositionScanner).processResponse(any());

        SerialPort mockedPort = mock(SerialPort.class);
        doReturn(true).when(mockedPort).openPort();
        doReturn(true).when(mockedPort).writeBytes(any());
        doReturn(new int[] {MotorControllerConstants.EXPECTED_RESPONSE_FROM_COMMAND}).when(mockedPort).readIntArray(1, Constants.MOTOR_CONTROLLER_TIMEOUT);

        PortHandler portHandler = new PortHandler(mockedPort);

        MotorControllerDigitalInputs digitalInputs = new MotorControllerDigitalInputs(portHandler);
        MotorControllerDigitalInputs spyDigitalInputs = spy(digitalInputs);
        doReturn(motorControllerRecord).when(spyDigitalInputs).check(any());

        MotorControllerMotorOutputs testMotorOutputs = new MotorControllerMotorOutputs(portHandler);
        MotorControllerMotorOutputs spyMotorOutputs = spy(testMotorOutputs);

        testMotorController.setPortHandler(portHandler);
        testMotorController.setDigitalInputs(spyDigitalInputs);
        testMotorController.setMotorOutputs(spyMotorOutputs);

        spyPositionScanner.setPortHandler(portHandler);

        TerminalReader mockedTerminalReader = mock(TerminalReader.class);
        doThrow(new RuntimeException()).when(mockedTerminalReader).run();

        Agv testAgv = new Agv(mockedTerminalReader, motorControllerRecord, positionScannerTestRecord, motorControllerLock);
        testAgv.setFileName(Constants.TEST_FILE_NAME);

        RunProgram runProgram = new RunProgram();
        runProgram.setAgv(testAgv);

        runProgram.setControllerRecord(motorControllerRecord);
        runProgram.setMotorController(testMotorController);
        runProgram.setPositionScanner(spyPositionScanner);
        runProgram.setTerminalReader(mockedTerminalReader);

        ScheduledExecutorService testExecutor = Executors.newScheduledThreadPool(1);
        Future future = testExecutor.submit(runProgram);
        Thread.sleep(SharedConstants.TEST_RUNTIME);
        future.cancel(true);

        motorControllerRecord.setDirection(Constants.STOP_STRING);
        motorControllerRecord.setMotor1Velocity(0);
        motorControllerRecord.setMotor2Velocity(0);
        motorControllerRecord.setNominalAccel(Constants.STOPPING_ACCELERATION);
        motorControllerRecord.setSafetyScannerMode(Constants.REGULAR_SCAN);
        motorControllerRecord.setMotor1Sign(Constants.MOTOR_POSITIVE);
        motorControllerRecord.setMotor2Sign(Constants.MOTOR_POSITIVE);
        motorControllerRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);

        String direction = runProgram.getMotorController().getControllerRecord().getDirection();
        double motor1Velocity = runProgram.getMotorController().getControllerRecord().getMotor1Velocity();
        double motor2Velocity = runProgram.getMotorController().getControllerRecord().getMotor2Velocity();
        double acceleration = runProgram.getMotorController().getControllerRecord().getNominalAccel();
        int scannerMode = runProgram.getMotorController().getControllerRecord().getSafetyScannerMode();
        String motor1Sign = runProgram.getMotorController().getControllerRecord().getMotor1Sign();
        String motor2Sign = runProgram.getMotorController().getControllerRecord().getMotor2Sign();
        int liftType = runProgram.getMotorController().getControllerRecord().getLiftType();

        Assertions.assertEquals(direction, Constants.STOP_STRING);
        Assertions.assertEquals(motor1Velocity, 0);
        Assertions.assertEquals(motor2Velocity, 0);
        Assertions.assertEquals(acceleration, Constants.STOPPING_ACCELERATION);
        Assertions.assertEquals(scannerMode, Constants.REGULAR_SCAN);
        Assertions.assertEquals(motor1Sign, Constants.MOTOR_POSITIVE);
        Assertions.assertEquals(motor2Sign, Constants.MOTOR_POSITIVE);
        Assertions.assertEquals(liftType, Constants.STOP_VERTICAL_CONSTANT);
        verify(spyMotorOutputs, atLeast(1)).write(eq(motorControllerRecord), any());
    }

    @Test
    public void positionScannerExceptionTest() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        MotorControllerRecord motorControllerRecord = new MotorControllerRecord();

        PositionScannerResponseRecord positionScannerTestRecord = new PositionScannerResponseRecord();
        positionScannerTestRecord.setTagId(0);
        positionScannerTestRecord.setOnTape(true);

        MotorController testMotorController = new MotorController("Test Port", motorControllerRecord, motorControllerLock);
        PositionScanner mockPositionScanner = mock(PositionScanner.class);
        doThrow(new RuntimeException()).when(mockPositionScanner).run();


        SerialPort mockedPort = mock(SerialPort.class);
        doReturn(true).when(mockedPort).openPort();
        doReturn(true).when(mockedPort).writeBytes(any());
        doReturn(new int[] {MotorControllerConstants.EXPECTED_RESPONSE_FROM_COMMAND}).when(mockedPort).readIntArray(1, Constants.MOTOR_CONTROLLER_TIMEOUT);

        PortHandler portHandler = new PortHandler(mockedPort);

        MotorControllerDigitalInputs digitalInputs = new MotorControllerDigitalInputs(portHandler);
        MotorControllerDigitalInputs spyDigitalInputs = spy(digitalInputs);
        doReturn(motorControllerRecord).when(spyDigitalInputs).check(any());

        MotorControllerMotorOutputs testMotorOutputs = new MotorControllerMotorOutputs(portHandler);
        MotorControllerMotorOutputs spyMotorOutputs = spy(testMotorOutputs);

        testMotorController.setPortHandler(portHandler);
        testMotorController.setDigitalInputs(spyDigitalInputs);
        testMotorController.setMotorOutputs(spyMotorOutputs);

        TerminalReader testTerminalReader = new TerminalReader();

        Agv testAgv = new Agv(testTerminalReader, motorControllerRecord, positionScannerTestRecord, motorControllerLock);
        testAgv.setFileName(Constants.TEST_FILE_NAME);

        RunProgram runProgram = new RunProgram();
        runProgram.setAgv(testAgv);

        runProgram.setControllerRecord(motorControllerRecord);
        runProgram.setMotorController(testMotorController);
        runProgram.setPositionScanner(mockPositionScanner);
        runProgram.setTerminalReader(testTerminalReader);

        ScheduledExecutorService testExecutor = Executors.newScheduledThreadPool(1);
        Future future = testExecutor.submit(runProgram);
        Thread.sleep(SharedConstants.TEST_RUNTIME);
        future.cancel(true);

        motorControllerRecord.setDirection(Constants.STOP_STRING);
        motorControllerRecord.setMotor1Velocity(0);
        motorControllerRecord.setMotor2Velocity(0);
        motorControllerRecord.setNominalAccel(Constants.STOPPING_ACCELERATION);
        motorControllerRecord.setSafetyScannerMode(Constants.REGULAR_SCAN);
        motorControllerRecord.setMotor1Sign(Constants.MOTOR_POSITIVE);
        motorControllerRecord.setMotor2Sign(Constants.MOTOR_POSITIVE);
        motorControllerRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);

        String direction = runProgram.getMotorController().getControllerRecord().getDirection();
        double motor1Velocity = runProgram.getMotorController().getControllerRecord().getMotor1Velocity();
        double motor2Velocity = runProgram.getMotorController().getControllerRecord().getMotor2Velocity();
        double acceleration = runProgram.getMotorController().getControllerRecord().getNominalAccel();
        int scannerMode = runProgram.getMotorController().getControllerRecord().getSafetyScannerMode();
        String motor1Sign = runProgram.getMotorController().getControllerRecord().getMotor1Sign();
        String motor2Sign = runProgram.getMotorController().getControllerRecord().getMotor2Sign();
        int liftType = runProgram.getMotorController().getControllerRecord().getLiftType();

        Assertions.assertEquals(direction, Constants.STOP_STRING);
        Assertions.assertEquals(motor1Velocity, 0);
        Assertions.assertEquals(motor2Velocity, 0);
        Assertions.assertEquals(acceleration, Constants.STOPPING_ACCELERATION);
        Assertions.assertEquals(scannerMode, Constants.REGULAR_SCAN);
        Assertions.assertEquals(motor1Sign, Constants.MOTOR_POSITIVE);
        Assertions.assertEquals(motor2Sign, Constants.MOTOR_POSITIVE);
        Assertions.assertEquals(liftType, Constants.STOP_VERTICAL_CONSTANT);
        verify(spyMotorOutputs, atLeast(1)).write(eq(motorControllerRecord), any());
    }

    @Test
    public void agvExceptionTest() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        MotorControllerRecord motorControllerRecord = new MotorControllerRecord();

        PositionScannerResponseRecord positionScannerTestRecord = new PositionScannerResponseRecord();
        positionScannerTestRecord.setTagId(0);
        positionScannerTestRecord.setOnTape(true);

        MotorController testMotorController = new MotorController("Test Port", motorControllerRecord, motorControllerLock);
        PositionScanner testPositionScanner = new PositionScanner("Test Port", positionScannerTestRecord);
        PositionScanner spyPositionScanner = spy(testPositionScanner);
        doReturn(new int[]{}).when(spyPositionScanner).receiveResponse();
        doReturn("Test").when(spyPositionScanner).processResponse(any());

        SerialPort mockedPort = mock(SerialPort.class);
        doReturn(true).when(mockedPort).openPort();
        doReturn(true).when(mockedPort).writeBytes(any());
        doReturn(new int[] {MotorControllerConstants.EXPECTED_RESPONSE_FROM_COMMAND}).when(mockedPort).readIntArray(1, Constants.MOTOR_CONTROLLER_TIMEOUT);

        PortHandler portHandler = new PortHandler(mockedPort);

        MotorControllerDigitalInputs digitalInputs = new MotorControllerDigitalInputs(portHandler);
        MotorControllerDigitalInputs spyDigitalInputs = spy(digitalInputs);
        doReturn(motorControllerRecord).when(spyDigitalInputs).check(any());

        MotorControllerMotorOutputs testMotorOutputs = new MotorControllerMotorOutputs(portHandler);
        MotorControllerMotorOutputs spyMotorOutputs = spy(testMotorOutputs);

        testMotorController.setPortHandler(portHandler);
        testMotorController.setDigitalInputs(spyDigitalInputs);
        testMotorController.setMotorOutputs(spyMotorOutputs);

        spyPositionScanner.setPortHandler(portHandler);

        TerminalReader testTerminalReader = new TerminalReader();

        Agv mockAgv = mock(Agv.class);
        doThrow(new RuntimeException()).when(mockAgv).run();


        RunProgram runProgram = new RunProgram();
        runProgram.setAgv(mockAgv);

        runProgram.setControllerRecord(motorControllerRecord);
        runProgram.setMotorController(testMotorController);
        runProgram.setPositionScanner(spyPositionScanner);
        runProgram.setTerminalReader(testTerminalReader);

        ScheduledExecutorService testExecutor = Executors.newScheduledThreadPool(1);
        Future future = testExecutor.submit(runProgram);
        Thread.sleep(SharedConstants.TEST_RUNTIME);
        future.cancel(true);

        motorControllerRecord.setDirection(Constants.STOP_STRING);
        motorControllerRecord.setMotor1Velocity(0);
        motorControllerRecord.setMotor2Velocity(0);
        motorControllerRecord.setNominalAccel(Constants.STOPPING_ACCELERATION);
        motorControllerRecord.setSafetyScannerMode(Constants.REGULAR_SCAN);
        motorControllerRecord.setMotor1Sign(Constants.MOTOR_POSITIVE);
        motorControllerRecord.setMotor2Sign(Constants.MOTOR_POSITIVE);
        motorControllerRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);

        String direction = runProgram.getMotorController().getControllerRecord().getDirection();
        double motor1Velocity = runProgram.getMotorController().getControllerRecord().getMotor1Velocity();
        double motor2Velocity = runProgram.getMotorController().getControllerRecord().getMotor2Velocity();
        double acceleration = runProgram.getMotorController().getControllerRecord().getNominalAccel();
        int scannerMode = runProgram.getMotorController().getControllerRecord().getSafetyScannerMode();
        String motor1Sign = runProgram.getMotorController().getControllerRecord().getMotor1Sign();
        String motor2Sign = runProgram.getMotorController().getControllerRecord().getMotor2Sign();
        int liftType = runProgram.getMotorController().getControllerRecord().getLiftType();

        Assertions.assertEquals(direction, Constants.STOP_STRING);
        Assertions.assertEquals(motor1Velocity, 0);
        Assertions.assertEquals(motor2Velocity, 0);
        Assertions.assertEquals(acceleration, Constants.STOPPING_ACCELERATION);
        Assertions.assertEquals(scannerMode, Constants.REGULAR_SCAN);
        Assertions.assertEquals(motor1Sign, Constants.MOTOR_POSITIVE);
        Assertions.assertEquals(motor2Sign, Constants.MOTOR_POSITIVE);
        Assertions.assertEquals(liftType, Constants.STOP_VERTICAL_CONSTANT);
        verify(spyMotorOutputs, atLeast(1)).write(eq(motorControllerRecord), any());
    }

    @Test
    public void motorControllerExceptionTest() throws SerialPortTimeoutException, SerialPortException, InterruptedException {
        MotorControllerRecord motorControllerTestRecord = new MotorControllerRecord();

        PositionScannerResponseRecord positionScannerTestRecord = new PositionScannerResponseRecord();
        positionScannerTestRecord.setTagId(0);
        positionScannerTestRecord.setOnTape(true);

        MotorController testMotorController = new MotorController("Test Port", motorControllerTestRecord, motorControllerLock);
        MotorController spyMotorController = spy(testMotorController);
        doThrow(new RuntimeException()).when(spyMotorController).run();

        PositionScanner testPositionScanner = new PositionScanner("Test Port", positionScannerTestRecord);
        PositionScanner spyPositionScanner = spy(testPositionScanner);
        doReturn(new int[]{}).when(spyPositionScanner).receiveResponse();
        doReturn("Test").when(spyPositionScanner).processResponse(any());

        SerialPort mockedPort = mock(SerialPort.class);
        doReturn(true).when(mockedPort).openPort();
        doReturn(true).when(mockedPort).writeBytes(any());
        doReturn(new int[] {MotorControllerConstants.EXPECTED_RESPONSE_FROM_COMMAND}).when(mockedPort).readIntArray(1, Constants.MOTOR_CONTROLLER_TIMEOUT);

        PortHandler portHandler = new PortHandler(mockedPort);

        MotorControllerDigitalInputs digitalInputs = new MotorControllerDigitalInputs(portHandler);
        MotorControllerDigitalInputs spyDigitalInputs = spy(digitalInputs);
        doReturn(motorControllerTestRecord).when(spyDigitalInputs).check(any());

        MotorControllerMotorOutputs testMotorOutputs = new MotorControllerMotorOutputs(portHandler);
        MotorControllerMotorOutputs spyMotorOutputs = spy(testMotorOutputs);

        spyMotorController.setPortHandler(portHandler);
        spyMotorController.setDigitalInputs(spyDigitalInputs);
        spyMotorController.setMotorOutputs(spyMotorOutputs);

        spyPositionScanner.setPortHandler(portHandler);

        TerminalReader testTerminalReader = new TerminalReader();

        Agv mockAgv = mock(Agv.class);
        doThrow(new RuntimeException()).when(mockAgv).run();


        RunProgram runProgram = new RunProgram();
        runProgram.setAgv(mockAgv);

        runProgram.setControllerRecord(motorControllerTestRecord);
        runProgram.setMotorController(spyMotorController);
        runProgram.setPositionScanner(spyPositionScanner);
        runProgram.setTerminalReader(testTerminalReader);

        ScheduledExecutorService testExecutor = Executors.newScheduledThreadPool(1);
        Future future = testExecutor.submit(runProgram);
        Thread.sleep(SharedConstants.TEST_RUNTIME);
        future.cancel(true);

        motorControllerTestRecord.setDirection(Constants.STOP_STRING);
        motorControllerTestRecord.setMotor1Velocity(0);
        motorControllerTestRecord.setMotor2Velocity(0);
        motorControllerTestRecord.setNominalAccel(Constants.STOPPING_ACCELERATION);
        motorControllerTestRecord.setSafetyScannerMode(Constants.REGULAR_SCAN);
        motorControllerTestRecord.setMotor1Sign(Constants.MOTOR_POSITIVE);
        motorControllerTestRecord.setMotor2Sign(Constants.MOTOR_POSITIVE);
        motorControllerTestRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);

        String direction = runProgram.getMotorController().getControllerRecord().getDirection();
        double motor1Velocity = runProgram.getMotorController().getControllerRecord().getMotor1Velocity();
        double motor2Velocity = runProgram.getMotorController().getControllerRecord().getMotor2Velocity();
        double acceleration = runProgram.getMotorController().getControllerRecord().getNominalAccel();
        int scannerMode = runProgram.getMotorController().getControllerRecord().getSafetyScannerMode();
        String motor1Sign = runProgram.getMotorController().getControllerRecord().getMotor1Sign();
        String motor2Sign = runProgram.getMotorController().getControllerRecord().getMotor2Sign();
        int liftType = runProgram.getMotorController().getControllerRecord().getLiftType();

        Assertions.assertEquals(direction, Constants.STOP_STRING);
        Assertions.assertEquals(motor1Velocity, 0);
        Assertions.assertEquals(motor2Velocity, 0);
        Assertions.assertEquals(acceleration, Constants.STOPPING_ACCELERATION);
        Assertions.assertEquals(scannerMode, Constants.REGULAR_SCAN);
        Assertions.assertEquals(motor1Sign, Constants.MOTOR_POSITIVE);
        Assertions.assertEquals(motor2Sign, Constants.MOTOR_POSITIVE);
        Assertions.assertEquals(liftType, Constants.STOP_VERTICAL_CONSTANT);
    }
}
