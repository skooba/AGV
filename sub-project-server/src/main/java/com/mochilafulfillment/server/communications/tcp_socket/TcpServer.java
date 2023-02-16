package com.mochilafulfillment.server.communications.tcp_socket;

import com.mochilafulfillment.server.dtos.RemoteGuiRecord;
import com.mochilafulfillment.shared.SharedConstants;
import com.mochilafulfillment.shared.TCPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class TcpServer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private TcpServerHandler tcpHandler;
    private RemoteGuiRecord remoteRecord;
    private BufferedReader inputStream;

    public TcpServer(int tcpPort, RemoteGuiRecord remoteRecord){
        this.tcpHandler = new TcpServerHandler(tcpPort);
        this.remoteRecord = remoteRecord;
    }

    public void writeToClient(String messageToClient){
        synchronized (this) {
            logger.debug("Writing TCP message to client " + messageToClient);
            tcpHandler.writeToServer(messageToClient);
        }
    }

    public void startTcpListener() {
        inputStream = tcpHandler.startServerListener();
        String inputLine;
        while (remoteRecord.isClientFinished() == false){
            try {
                if((inputLine = inputStream.readLine()) != null){
                    logger.debug("Message received from client: " + inputLine);
                    processTcpInput(inputLine);
                    remoteRecord.setNewRemoteGuiToAGVRecord(true);
                }
            } catch (IOException e) {
                throw new TCPException(e);
            }
        }
        tcpHandler.close();
    }

    public void processTcpInput(String inputLine) {
        switch (inputLine){
            case SharedConstants.TCP_U_STRING:
                remoteRecord.setForksUp(true);
                break;
            case SharedConstants.TCP_D_STRING:
                remoteRecord.setForksDown(true);
                break;
            case SharedConstants.TCP_LEFT_STRING:
                remoteRecord.setMoveCCW(true);
                break;
            case SharedConstants.TCP_RIGHT_STRING:
                remoteRecord.setMoveCW(true);
                break;
            case SharedConstants.TCP_UP_STRING:
                remoteRecord.setMoveForward(true);
                break;
            case SharedConstants.TCP_DOWN_STRING:
                remoteRecord.setMoveBackwards(true);
                break;
            case SharedConstants.TCP_STOP_STRING:
                remoteRecord.setForksUp(false);
                remoteRecord.setForksDown(false);
                remoteRecord.setMoveCCW(false);
                remoteRecord.setMoveCW(false);
                remoteRecord.setMoveForward(false);
                remoteRecord.setMoveBackwards(false);
                break;
            case SharedConstants.TCP_VELOCITY_STRING:
                int velocity = 0; // read another line, velocity value is next
                try {
                    velocity = Integer.parseInt(inputStream.readLine());
                } catch (IOException e) {
                    throw new TCPException(e);
                }
                remoteRecord.setVelocity(velocity);
                break;
            case SharedConstants.TCP_ROTATION_VELOCITY_STRING:
                int rotVelocity = 0; // read another line, acceleration value is next
                try {
                    rotVelocity = Integer.parseInt(inputStream.readLine());
                } catch (IOException e) {
                    throw new TCPException(e);
                }
                remoteRecord.setRotationVelocity(rotVelocity);
                break;
            case SharedConstants.TCP_ACCELERATION_STRING:
                int acceleration = 0; // read another line, acceleration value is next
                try {
                    acceleration = Integer.parseInt(inputStream.readLine());
                } catch (IOException e) {
                    throw new TCPException(e);
                }
                remoteRecord.setAcceleration(acceleration);
                break;
            case SharedConstants.TCP_ROTATION_ACCELERATION_STRING:
                int rotAcceleration = 0; // read another line, acceleration value is next
                try {
                    rotAcceleration = Integer.parseInt(inputStream.readLine());
                } catch (IOException e) {
                    throw new TCPException(e);
                }
                remoteRecord.setRotationAcceleration(rotAcceleration);
                break;
            case SharedConstants.TCP_SHUTDOWN_STRING:
                remoteRecord.setClientFinished(true);
                break;
            default:
                throw new TCPException("Received unexpected TCP data from Client ", new IllegalArgumentException(inputLine + " is not a valid received"));
        }
    }

    public TcpServerHandler getTcpHandler() {
        return tcpHandler;
    }

    public void setTcpHandler(TcpServerHandler tcpHandler) {
        this.tcpHandler = tcpHandler;
    }

    public void setRemoteRecord(RemoteGuiRecord remoteRecord) {
        this.remoteRecord = remoteRecord;
    }

    public void setInputStream(BufferedReader inputStream) {
        this.inputStream = inputStream;
    }

    public RemoteGuiRecord getRemoteRecord() {
        return remoteRecord;
    }
}