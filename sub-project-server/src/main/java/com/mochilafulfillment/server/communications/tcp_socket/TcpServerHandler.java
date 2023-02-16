package com.mochilafulfillment.server.communications.tcp_socket;

import com.mochilafulfillment.shared.SharedConstants;
import com.mochilafulfillment.shared.TCPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServerHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader bufferedInputStream;
    private PrintWriter writer;
    OutputStream outputStream;

    public TcpServerHandler(int tcpPort) {
        logger.debug("Starting TCP server socket");

        try {
            serverSocket = new ServerSocket(tcpPort);
            logger.debug("Server socket created");
        } catch (IOException e) {
            throw new TCPException("Failed to create server socket on port: " + SharedConstants.TCP_PORT);
        }

        try {
            socket = serverSocket.accept(); //listens for client and accepts (blocking)
            logger.debug("Server listening socket created");
        } catch (IOException e) {
            throw new TCPException("An IO occurred exception while waiting for connection",e);
        }

        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
            logger.debug("Server printWriter created");
        } catch (IOException e) {
            throw new TCPException("An IOException occurred exception while creating PrintWriter",e);
        }

        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            throw new TCPException(e);
        }

    }

    public BufferedReader startServerListener(){
        logger.debug("TCP server listener starting");
        InputStreamReader inputStreamReader;
        try {
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            logger.debug("Server Input Stream Created");
        } catch (IOException e) {
            throw new TCPException("An IO occurred exception while waiting for connection",e);
        }
        bufferedInputStream = new BufferedReader(inputStreamReader);
        return bufferedInputStream;
    }

    public void close() {
        synchronized (this) {
            try {
                logger.debug("Closing down TCP server socket");
//                bufferedInputStream.close();
                writer.close();
                socket.close();
                serverSocket.close();
                outputStream.close();
            } catch (IOException e) {
                throw new TCPException("IO Exception while closing TCP server socket");
            }
        }
    }

    public void writeToServer(String messageToServer){
        synchronized (this) {
            logger.debug("Writing TCP message to server " + messageToServer);
            writer.println(messageToServer);
            writer.flush();
        }
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public Socket getSocket() {
        return socket;
    }

    public BufferedReader getInputStream() {
        return bufferedInputStream;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public void setWriter(PrintWriter writer) {
        this.writer = writer;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public BufferedReader getBufferedInputStream() {
        return bufferedInputStream;
    }

    public void setBufferedInputStream(BufferedReader bufferedInputStream) {
        this.bufferedInputStream = bufferedInputStream;
    }
}
