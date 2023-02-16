package com.mochilafulfillment.server.communications.tcp_socket;

import com.mochilafulfillment.server.dtos.RemoteGuiRecord;
import com.mochilafulfillment.shared.SharedConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static org.mockito.Mockito.*;

public class TcpServerTest {

    TcpServer tcpServer;
    BufferedReader bufferedReader2;

    @BeforeEach
    public void init() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(SharedConstants.TEST_RUNTIME);
                    Socket testSocket = new Socket("localhost", SharedConstants.TCP_PORT);
                    PrintWriter printWriter2 = new PrintWriter(testSocket.getOutputStream());
                    printWriter2.println("this is a test1");
                    printWriter2.println(SharedConstants.TCP_SHUTDOWN_STRING);
                    printWriter2.flush();
                    InputStreamReader inputStreamReader2 = new InputStreamReader(testSocket.getInputStream());
                    bufferedReader2 = new BufferedReader(inputStreamReader2);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
        tcpServer = new TcpServer(SharedConstants.TCP_PORT, new RemoteGuiRecord());
    }

    @Test
    public void writeToClientTest() throws IOException {
        Socket socket1 = tcpServer.getTcpHandler().getSocket();

        InputStreamReader inputStreamReader = new InputStreamReader(socket1.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String string1 = bufferedReader.readLine();

        PrintWriter printWriter = new PrintWriter(socket1.getOutputStream());
        printWriter.println("this is a test2");
        printWriter.flush();

        String string2 = bufferedReader2.readLine();

        Assertions.assertEquals(string1, "this is a test1");
        Assertions.assertEquals(string2, "this is a test2");
    }

    @Test
    public void processTcpInputForwardTest(){
        RemoteGuiRecord remoteGuiRecord = new RemoteGuiRecord();
        tcpServer.setRemoteRecord(remoteGuiRecord);
        tcpServer.processTcpInput(SharedConstants.TCP_UP_STRING);


        RemoteGuiRecord testGuiRecord = new RemoteGuiRecord();
        testGuiRecord.setMoveForward(true);

        Assertions.assertEquals(remoteGuiRecord,testGuiRecord);
    }

    @Test
    public void processTcpInputCWTest(){
        RemoteGuiRecord remoteGuiRecord = new RemoteGuiRecord();
        tcpServer.setRemoteRecord(remoteGuiRecord);
        tcpServer.processTcpInput(SharedConstants.TCP_RIGHT_STRING);


        RemoteGuiRecord testGuiRecord = new RemoteGuiRecord();
        testGuiRecord.setMoveCW(true);

        Assertions.assertEquals(remoteGuiRecord,testGuiRecord);
    }
    @Test
    public void processTcpInputCCWTest(){
        RemoteGuiRecord remoteGuiRecord = new RemoteGuiRecord();
        tcpServer.setRemoteRecord(remoteGuiRecord);
        tcpServer.processTcpInput(SharedConstants.TCP_LEFT_STRING);


        RemoteGuiRecord testGuiRecord = new RemoteGuiRecord();
        testGuiRecord.setMoveCCW(true);

        Assertions.assertEquals(remoteGuiRecord,testGuiRecord);
    }
    @Test
    public void processTcpInputBackwardsTest(){
        RemoteGuiRecord remoteGuiRecord = new RemoteGuiRecord();
        tcpServer.setRemoteRecord(remoteGuiRecord);
        tcpServer.processTcpInput(SharedConstants.TCP_DOWN_STRING);


        RemoteGuiRecord testGuiRecord = new RemoteGuiRecord();
        testGuiRecord.setMoveBackwards(true);

        Assertions.assertEquals(remoteGuiRecord,testGuiRecord);
    }
    @Test
    public void processTcpInputForksUpTest(){
        RemoteGuiRecord remoteGuiRecord = new RemoteGuiRecord();
        tcpServer.setRemoteRecord(remoteGuiRecord);
        tcpServer.processTcpInput(SharedConstants.TCP_U_STRING);


        RemoteGuiRecord testGuiRecord = new RemoteGuiRecord();
        testGuiRecord.setForksUp(true);

        Assertions.assertEquals(remoteGuiRecord,testGuiRecord);
    }
    @Test
    public void processTcpInputForksDownTest(){
        RemoteGuiRecord remoteGuiRecord = new RemoteGuiRecord();
        tcpServer.setRemoteRecord(remoteGuiRecord);
        tcpServer.processTcpInput(SharedConstants.TCP_D_STRING);


        RemoteGuiRecord testGuiRecord = new RemoteGuiRecord();
        testGuiRecord.setForksDown(true);

        Assertions.assertEquals(remoteGuiRecord,testGuiRecord);
    }
    @Test
    public void processTcpInputStopTest(){
        RemoteGuiRecord remoteGuiRecord = new RemoteGuiRecord();
        remoteGuiRecord.setForksDown(true);
        remoteGuiRecord.setForksUp(true);
        remoteGuiRecord.setMoveForward(true);
        remoteGuiRecord.setMoveBackwards(true);
        remoteGuiRecord.setMoveCCW(true);
        remoteGuiRecord.setMoveCW(true);

        tcpServer.setRemoteRecord(remoteGuiRecord);
        tcpServer.processTcpInput(SharedConstants.TCP_STOP_STRING);


        RemoteGuiRecord testGuiRecord = new RemoteGuiRecord();

        Assertions.assertEquals(remoteGuiRecord,testGuiRecord);
    }

    @Test
    public void processTcpInputChangeVelocityTest() throws IOException {
        Socket socket1 = tcpServer.getTcpHandler().getSocket();

        PrintWriter printWriter = new PrintWriter(socket1.getOutputStream());
        printWriter.println("62");
        printWriter.flush();

        tcpServer.setInputStream(bufferedReader2);

        RemoteGuiRecord remoteGuiRecord = new RemoteGuiRecord();
        tcpServer.setRemoteRecord(remoteGuiRecord);
        tcpServer.processTcpInput(SharedConstants.TCP_VELOCITY_STRING);


        RemoteGuiRecord testGuiRecord = new RemoteGuiRecord();
        testGuiRecord.setVelocity(62);

        Assertions.assertEquals(remoteGuiRecord,testGuiRecord);
    }

    @Test
    public void processTcpInputChangeAccelTest() throws IOException {
        Socket socket1 = tcpServer.getTcpHandler().getSocket();

        PrintWriter printWriter = new PrintWriter(socket1.getOutputStream());
        printWriter.println("49");
        printWriter.flush();

        tcpServer.setInputStream(bufferedReader2);

        RemoteGuiRecord remoteGuiRecord = new RemoteGuiRecord();
        tcpServer.setRemoteRecord(remoteGuiRecord);
        tcpServer.processTcpInput(SharedConstants.TCP_ACCELERATION_STRING);


        RemoteGuiRecord testGuiRecord = new RemoteGuiRecord();
        testGuiRecord.setAcceleration(49);

        Assertions.assertEquals(remoteGuiRecord,testGuiRecord);
    }

    @Test
    public void processTcpInputChangeVelocityRotationTest() throws IOException {
        Socket socket1 = tcpServer.getTcpHandler().getSocket();

        PrintWriter printWriter = new PrintWriter(socket1.getOutputStream());
        printWriter.println("59");
        printWriter.flush();

        tcpServer.setInputStream(bufferedReader2);

        RemoteGuiRecord remoteGuiRecord = new RemoteGuiRecord();
        tcpServer.setRemoteRecord(remoteGuiRecord);
        tcpServer.processTcpInput(SharedConstants.TCP_ROTATION_VELOCITY_STRING);


        RemoteGuiRecord testGuiRecord = new RemoteGuiRecord();
        testGuiRecord.setRotationVelocity(59);

        Assertions.assertEquals(remoteGuiRecord,testGuiRecord);
    }

    @Test
    public void processTcpInputChangeAccelRotationTest() throws IOException {
        Socket socket1 = tcpServer.getTcpHandler().getSocket();

        PrintWriter printWriter = new PrintWriter(socket1.getOutputStream());
        printWriter.println("67");
        printWriter.flush();

        tcpServer.setInputStream(bufferedReader2);

        RemoteGuiRecord remoteGuiRecord = new RemoteGuiRecord();
        tcpServer.setRemoteRecord(remoteGuiRecord);
        tcpServer.processTcpInput(SharedConstants.TCP_ROTATION_ACCELERATION_STRING);


        RemoteGuiRecord testGuiRecord = new RemoteGuiRecord();
        testGuiRecord.setRotationAcceleration(67);

        Assertions.assertEquals(remoteGuiRecord,testGuiRecord);
    }

    @Test
    public void processTcpInputShutdownTest(){
        RemoteGuiRecord remoteGuiRecord = new RemoteGuiRecord();
        tcpServer.setRemoteRecord(remoteGuiRecord);
        tcpServer.processTcpInput(SharedConstants.TCP_SHUTDOWN_STRING);


        RemoteGuiRecord testGuiRecord = new RemoteGuiRecord();
        testGuiRecord.setClientFinished(true);


        Assertions.assertEquals(remoteGuiRecord,testGuiRecord);
    }

    @Test
    public void StartTcpListenerTest() throws IOException, InterruptedException {
        tcpServer.getTcpHandler().close();
        bufferedReader2.close();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(SharedConstants.TEST_RUNTIME);
                    Socket testSocket = new Socket("localhost", SharedConstants.TCP_PORT);
                    PrintWriter printWriter = new PrintWriter(testSocket.getOutputStream());
                    printWriter.println(SharedConstants.TCP_SHUTDOWN_STRING);
                    printWriter.flush();
                    InputStreamReader inputStreamReader2 = new InputStreamReader(testSocket.getInputStream());
                    bufferedReader2 = new BufferedReader(inputStreamReader2);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
        tcpServer = new TcpServer(SharedConstants.TCP_PORT, new RemoteGuiRecord());

        TcpServer spyTcpServer = spy(tcpServer);

        TcpServerHandler tcpHandler = spyTcpServer.getTcpHandler();
        TcpServerHandler spyTcpHandler = spy(tcpHandler);
        spyTcpServer.setTcpHandler(spyTcpHandler);

        tcpServer.startTcpListener();

        RemoteGuiRecord testRecord = new RemoteGuiRecord();
        testRecord.setClientFinished(true);
        testRecord.setNewRemoteGuiToAGVRecord(true);

        Assertions.assertEquals(tcpServer.getRemoteRecord(), testRecord);
    }

    @AfterEach
    public void close() throws IOException {
        tcpServer.getTcpHandler().close();
        bufferedReader2.close();
    }
}
