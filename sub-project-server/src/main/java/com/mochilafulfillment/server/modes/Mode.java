package com.mochilafulfillment.server.modes;

import com.mochilafulfillment.server.agv_utils.Exceptions.PGVException;
import com.mochilafulfillment.server.api.laptop.TerminalReader;
import com.mochilafulfillment.server.dtos.AgvOutputsRecord;
import com.mochilafulfillment.server.motor_controller.dtos.MotorControllerRecord;
import com.mochilafulfillment.server.position_scanner.dtos.PositionScannerResponseRecord;

// Client that calls all services for particular mode
// Handles DTO (data transfer object) between services
public interface Mode {
    AgvOutputsRecord run(AgvOutputsRecord outputRecord, PositionScannerResponseRecord positionScannerRecord, MotorControllerRecord controllerRecord, TerminalReader terminalReader) throws InterruptedException, PGVException;
    AgvOutputsRecord endMode(AgvOutputsRecord outputRecord);
}
