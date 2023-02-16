package com.mochilafulfillment.server.modes;

import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.api.laptop.TerminalReader;
import com.mochilafulfillment.server.dtos.AgvOutputsRecord;
import com.mochilafulfillment.server.dtos.RemoteGuiRecord;
import com.mochilafulfillment.server.motor_controller.dtos.MotorControllerRecord;
import com.mochilafulfillment.server.position_scanner.dtos.PositionScannerResponseRecord;
import com.mochilafulfillment.server.communications.tcp_socket.TcpServer;
import com.mochilafulfillment.shared.SharedConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteControlMode implements Mode {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private RemoteGuiRecord remoteRecord = new RemoteGuiRecord();
    public int tcpPort;
    private TcpServer tcpServer;

    public RemoteControlMode(int tcpPort){
        logger.debug("Entering Remote Control Mode");
        this.tcpPort = tcpPort;
    }


    // Create new AgvOutputsRecord based on current laptop input conditions
    @Override
    public AgvOutputsRecord run(AgvOutputsRecord outputRecord, PositionScannerResponseRecord positionScanner, MotorControllerRecord controllerRecord, TerminalReader terminalReader) throws InterruptedException {
        if(remoteRecord.isFirstPassInRemoteControlMode()) {
            launchTcpListener();
            tcpServer.writeToClient(SharedConstants.TCP_START_STRING);
            remoteRecord.setFirstPassInRemoteControlMode(false);
        }

        //Don't change the mode until "2" is entered into terminal reader
        if (remoteRecord.isClientFinished()) {
            outputRecord = endMode(outputRecord);
            return outputRecord;
        } else if (terminalReader.isExitRemoteMode() == true) {
            logger.debug("Sending message to client to end application");
            tcpServer.writeToClient(SharedConstants.TCP_SHUTDOWN_STRING);
            terminalReader.setExitRemoteMode(false);
            remoteRecord.setClientFinished(true);
            outputRecord = endMode(outputRecord);
        } else if (remoteRecord.isNewRemoteGuiToAgvRecord()) {
            logger.debug("Updating AGV record based on new data from GUI");
            setAGVRecordBasedOnRemoteCommand(outputRecord);
            remoteRecord.setNewRemoteGuiToAGVRecord(false);
        } else {
            logger.debug("No new data received from GUI");
        }

        return outputRecord;
    }

    @Override
    public AgvOutputsRecord endMode(AgvOutputsRecord outputRecord) {
        logger.debug("Exiting remote control mode");
        outputRecord.setModeFinished(true);
        return outputRecord;
    }

    public AgvOutputsRecord setAGVRecordBasedOnRemoteCommand(AgvOutputsRecord outputRecord) {
        if(remoteRecord.isForksUp()){
            outputRecord.setLiftType(Constants.LIFT_CONSTANT);
        } else if (remoteRecord.isForksDown()){
            outputRecord.setLiftType(Constants.LOWER_CONSTANT);
        } else {
            outputRecord.setLiftType(Constants.STOP_VERTICAL_CONSTANT);
        }
        if(remoteRecord.isMoveForward()){
            outputRecord.setDirection(Constants.FORWARDS_STRING);
            outputRecord.setNominalAccel(remoteRecord.getAcceleration());
            outputRecord.setNominalVelocity(remoteRecord.getVelocity());
        } else if(remoteRecord.isMoveBackwards()){
            outputRecord.setDirection(Constants.BACKWARDS_STRING);
            outputRecord.setNominalAccel(remoteRecord.getAcceleration());
            outputRecord.setNominalVelocity(remoteRecord.getVelocity());
        } else if(remoteRecord.isMoveCW()){ //speed for CW and CCW are set to a constant value and will not be effected by gui
            outputRecord.setDirection(Constants.CW_STRING);
            outputRecord.setRotationVelocity(remoteRecord.getRotationVelocity());
            outputRecord.setRotationAcceleration(remoteRecord.getRotationAcceleration());
        } else if(remoteRecord.isMoveCCW()){
            outputRecord.setDirection(Constants.CCW_STRING);
            outputRecord.setRotationVelocity(remoteRecord.getRotationVelocity());
            outputRecord.setRotationAcceleration(remoteRecord.getRotationAcceleration());
        } else {
            outputRecord.setDirection(Constants.STOP_STRING);
        }
        return outputRecord;
    }

    public void launchTcpListener(){
        logger.debug("Starting TCP server for communication with Client");
        this.tcpServer = new TcpServer(tcpPort, remoteRecord);
        new Thread(new Runnable() {
            @Override
            public void run() {
                tcpServer.startTcpListener();
            }
        }).start();
    }

    public void setRemoteRecord(RemoteGuiRecord remoteRecord) {
        this.remoteRecord = remoteRecord;
    }

    public RemoteGuiRecord getRemoteRecord(){
        return this.remoteRecord;
    }

    public void setTcpServer(TcpServer tcpServer) {this.tcpServer = tcpServer;}

}
