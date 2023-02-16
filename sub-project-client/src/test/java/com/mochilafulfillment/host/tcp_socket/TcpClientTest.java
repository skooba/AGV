package com.mochilafulfillment.host.tcp_socket;

import com.mochilafulfillment.host.dtos.ClientRecord;
import com.mochilafulfillment.shared.SharedConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static org.mockito.Mockito.*;

public class TcpClientTest {

    ServerSocket serverSocket;
    TcpClient tcpClient;
    @BeforeEach
    public void init() throws IOException {
        serverSocket = new ServerSocket(SharedConstants.TCP_PORT);
        tcpClient = new TcpClient("localhost", SharedConstants.TCP_PORT, new ClientRecord());
    }

    @Test
    public void writeToServerTest() throws IOException {
        tcpClient.writeToServer("this is a test");

        Socket testSocket = serverSocket.accept();
        InputStreamReader inputStreamReader = new InputStreamReader(testSocket.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String string1 = bufferedReader.readLine();

        PrintWriter printWriter = new PrintWriter(testSocket.getOutputStream());
        printWriter.println("this is a test2");
        printWriter.flush();

        Assertions.assertEquals(string1, "this is a test");
    }

    @Test
    public void processTcpInputStartTest(){
        ClientRecord clientRecord = new ClientRecord();
        tcpClient.setClientRecord(clientRecord);
        tcpClient.processTcpInput(SharedConstants.TCP_START_STRING);

        ClientRecord testClientRecord = new ClientRecord();
        testClientRecord.setStartGui(true);

        Assertions.assertEquals(clientRecord,testClientRecord);
    }

    @Test
    public void processTcpInputStopTest(){
        ClientRecord clientRecord = new ClientRecord();
        tcpClient.setClientRecord(clientRecord);
        tcpClient.processTcpInput(SharedConstants.TCP_SHUTDOWN_STRING);

        ClientRecord testClientRecord = new ClientRecord();
        testClientRecord.setEndGui(true);

        Assertions.assertEquals(clientRecord,testClientRecord);
    }

    @Test
    public void StartTcpListenerTest() throws IOException {
        Socket testSocket = serverSocket.accept();
        PrintWriter printWriter = new PrintWriter(testSocket.getOutputStream());
        printWriter.println(SharedConstants.TCP_SHUTDOWN_STRING);
        printWriter.flush();

        TcpClient spyTcpClient = spy(tcpClient);

        TcpClientHandler tcpHandler = spyTcpClient.getTcpHandler();
        TcpClientHandler spyTcpHandler = spy(tcpHandler);
        spyTcpClient.setTcpHandler(spyTcpHandler);

        spyTcpClient.startTcpListener();

        verify(spyTcpClient).processTcpInput(SharedConstants.TCP_SHUTDOWN_STRING);
        verify(spyTcpHandler).close();
    }

    @AfterEach
    public void close() throws IOException {
        serverSocket.close();
    }
}
