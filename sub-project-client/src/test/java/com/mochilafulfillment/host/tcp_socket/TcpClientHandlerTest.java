package com.mochilafulfillment.host.tcp_socket;

import com.mochilafulfillment.host.client_utils.Constants;
import com.mochilafulfillment.shared.SharedConstants;
import com.mochilafulfillment.shared.TCPException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpClientHandlerTest {

    TcpClientHandler tcpClientHandler;
    ServerSocket serverSocket;
    @BeforeEach
    public void init() throws IOException {
        serverSocket = new ServerSocket(SharedConstants.TCP_PORT);
        tcpClientHandler = new TcpClientHandler("localhost", SharedConstants.TCP_PORT);
    }

    @Test
    public void socketCreationErrorTest(){
        Assertions.assertThrows(TCPException.class, () -> tcpClientHandler = new TcpClientHandler("this is a test", SharedConstants.TCP_PORT));

    }

    @Test
    public void startClientListenerTest(){
        Assertions.assertDoesNotThrow(()-> tcpClientHandler.startClientListener());
    }

    @Test
    public void tcpHandlerWriteTest() throws IOException {
        tcpClientHandler.writeToServer("this is a test1");
        BufferedReader bufferedInputStream = tcpClientHandler.startClientListener();

        Socket testSocket = serverSocket.accept();
        InputStreamReader inputStreamReader = new InputStreamReader(testSocket.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String string1 = bufferedReader.readLine();

        PrintWriter printWriter = new PrintWriter(testSocket.getOutputStream());
        printWriter.println("this is a test2");
        printWriter.flush();

        String string2 = bufferedInputStream.readLine();

        Assertions.assertEquals(string1, "this is a test1");
        Assertions.assertEquals(string2, "this is a test2");
    }

    @Test
    public void tcpHandlerWorkingTest(){
        tcpClientHandler.startClientListener();

        BufferedReader inputStream = tcpClientHandler.getInputStream();
        OutputStream outputStream = tcpClientHandler.getOutputStream();
        Socket socket = tcpClientHandler.getSocket();

        Assertions.assertFalse(socket.isClosed());
        Assertions.assertDoesNotThrow(() ->outputStream.write(550));
        Assertions.assertDoesNotThrow(() ->inputStream.ready());
    }

    @Test
    public void closeTcpHandlerTest() {
        tcpClientHandler.startClientListener();

        OutputStream outputStream = tcpClientHandler.getOutputStream();
        Socket socket = tcpClientHandler.getSocket();

        tcpClientHandler.close();

        Assertions.assertTrue(socket.isClosed());
        Assertions.assertThrows(IOException.class, () ->outputStream.write(550));
    }

    @AfterEach
    public void close() throws IOException {
        serverSocket.close();
        tcpClientHandler.close();
    }

}
