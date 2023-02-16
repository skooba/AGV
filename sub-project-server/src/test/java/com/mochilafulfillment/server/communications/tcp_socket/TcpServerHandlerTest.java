package com.mochilafulfillment.server.communications.tcp_socket;

import com.mochilafulfillment.shared.SharedConstants;
import com.mochilafulfillment.shared.TCPException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

public class TcpServerHandlerTest {

    TcpServerHandler tcpClientHandler;
    BufferedReader bufferedReader2;
    Socket testSocket;
    @BeforeEach
    public void init() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(SharedConstants.TEST_RUNTIME);
                    testSocket = new Socket("localhost", SharedConstants.TCP_PORT);
                    PrintWriter printWriter2 = new PrintWriter(testSocket.getOutputStream());
                    printWriter2.println("this is a test1");
                    printWriter2.flush();
                    InputStreamReader inputStreamReader2 = new InputStreamReader(testSocket.getInputStream());
                    bufferedReader2 = new BufferedReader(inputStreamReader2);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
        tcpClientHandler = new TcpServerHandler(SharedConstants.TCP_PORT);
    }

    @Test
    public void socketCreationErrorTest(){
        Assertions.assertThrows(TCPException.class, () -> tcpClientHandler = new TcpServerHandler(SharedConstants.TCP_PORT));

    }

    @Test
    public void startClientListenerTest(){
        Assertions.assertDoesNotThrow(()-> tcpClientHandler.startServerListener());
    }

    @Test
    public void tcpHandlerWriteTest() throws IOException {
        Socket socket1 = tcpClientHandler.getSocket();

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
    public void tcpHandlerWorkingTest(){
        tcpClientHandler.startServerListener();

        BufferedReader inputStream = tcpClientHandler.getInputStream();
        OutputStream outputStream = tcpClientHandler.getOutputStream();
        Socket socket = tcpClientHandler.getSocket();

        Assertions.assertFalse(socket.isClosed());
        Assertions.assertDoesNotThrow(() ->outputStream.write(550));
        Assertions.assertDoesNotThrow(() ->inputStream.ready());
    }

    @Test
    public void closeTcpHandlerTest() {
        tcpClientHandler.startServerListener();

        OutputStream outputStream = tcpClientHandler.getOutputStream();
        Socket socket = tcpClientHandler.getSocket();

        tcpClientHandler.close();

        Assertions.assertTrue(socket.isClosed());
        Assertions.assertThrows(IOException.class, () ->outputStream.write(550));
    }

    @AfterEach
    public void close() throws IOException {
        testSocket.close();
//        tcpClientHandler.getServerSocket().close();
//        tcpClientHandler.getSocket().close();
        tcpClientHandler.close();
    }

}
