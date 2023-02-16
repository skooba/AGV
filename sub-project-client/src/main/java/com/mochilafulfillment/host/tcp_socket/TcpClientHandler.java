package com.mochilafulfillment.host.tcp_socket;

import com.mochilafulfillment.shared.TCPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class TcpClientHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Socket socket;
    private BufferedReader bufferedInputStream;
    private PrintWriter writer;
    private OutputStream outputStream;

    public TcpClientHandler(String hostAddress, int tcpPort){
        logger.debug("Starting TCP client socket");
        try {
            socket = new Socket(hostAddress, tcpPort);
            logger.debug("Client socket created");
        } catch (IOException e) {
            throw new TCPException("Failed to create server socket on port: " + tcpPort + " at host address: " + hostAddress);
        }

        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
            logger.debug("Client printWriter created");
        } catch (IOException e) {
            throw new TCPException("An IO occurred exception while waiting for connection",e);
        }

        try {
            outputStream = socket.getOutputStream();
            logger.debug("Client output stream created");
        } catch (IOException e) {
            throw new TCPException(e);
        }

    }


    public BufferedReader startClientListener(){
        logger.debug("TCP client listener starting");
        InputStreamReader inputStreamReader;
        try {
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            logger.debug("Client Input Stream Created");
        } catch (IOException e) {
            throw new TCPException("An IO occurred exception while waiting for connection",e);
        }
        bufferedInputStream = new BufferedReader(inputStreamReader);
        return bufferedInputStream;
    }

    public void close() {
        synchronized (this) {
            try {
                logger.debug("Closing down TCP client");
                writer.close();
                socket.close();
                outputStream.close();
            }
            catch (IOException e) {
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

    public Socket getSocket() {
        return socket;
    }

    public BufferedReader getInputStream() {
        return bufferedInputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }
}
