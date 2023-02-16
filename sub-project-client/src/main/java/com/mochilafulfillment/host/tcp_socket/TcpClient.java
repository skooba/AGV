package com.mochilafulfillment.host.tcp_socket;

import com.mochilafulfillment.host.client_utils.Constants;
import com.mochilafulfillment.host.dtos.ClientRecord;
import com.mochilafulfillment.shared.SharedConstants;
import com.mochilafulfillment.shared.TCPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;

public class TcpClient {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private TcpClientHandler tcpHandler;
    private BufferedReader inputStream;
    private ClientRecord clientRecord;

    public TcpClient(String hostAddress, int tcpPort, ClientRecord clientRecord){
        this.tcpHandler = new TcpClientHandler(hostAddress, tcpPort);
        this.clientRecord = clientRecord;
    }

    public void startTcpListener(){
        inputStream = tcpHandler.startClientListener();
        String inputLine;
        while (this.clientRecord.isEndGui() == false){
            try {
                if((inputLine = inputStream.readLine()) != null){
                    logger.debug("Message received from server: " + inputLine);
                    processTcpInput(inputLine);
                }
            } catch (IOException | TCPException e) {
                throw new TCPException(e);
            }
        }
        tcpHandler.close();
    }

    public void processTcpInput(String inputLine) {
        switch (inputLine){
            case SharedConstants.TCP_START_STRING:
                clientRecord.setStartGui(true);
                break;
            case SharedConstants.TCP_SHUTDOWN_STRING:
                clientRecord.setEndGui(true);
                break;
            default:
                throw new TCPException("Received unexpected TCP data from Server", new IllegalArgumentException(inputLine + " is not a valid received"));
        }
    }

    public void writeToServer(String messageToServer){
        synchronized (this) {
            tcpHandler.writeToServer(messageToServer);
        }
    }

    public void setClientRecord(ClientRecord clientRecord) {
        this.clientRecord = clientRecord;
    }

    public void setTcpHandler(TcpClientHandler tcpHandler) {
        this.tcpHandler = tcpHandler;
    }

    public TcpClientHandler getTcpHandler() {
        return tcpHandler;
    }
}
