package com.mochilafulfillment.server.communications.serial_port;

import jssc.SerialPortException;

public class IncorrectResponseFromSerialPortException extends SerialPortException {

    public IncorrectResponseFromSerialPortException(String portName, String methodName, String message) {
        super(portName, methodName, message);
    }
}
