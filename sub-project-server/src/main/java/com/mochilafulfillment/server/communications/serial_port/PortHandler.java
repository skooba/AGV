package com.mochilafulfillment.server.communications.serial_port;

import com.mochilafulfillment.server.agv_utils.ByteToolbox;
import com.mochilafulfillment.server.agv_utils.Constants;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class PortHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SerialPort serialPort;
    private int parity;
    private int baudRate;

    public PortHandler(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    public void run(int baudRate, int parity) throws SerialPortException {
        this.baudRate = baudRate;
        this.parity = parity;
        serialPort.openPort();
        serialPort.setParams(baudRate,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                parity);
        logger.debug("Serial port" + serialPort.getPortName() + " initialized, with a baud rate of " + baudRate);
    }

    //using byte[] so no values out of range are used
    public boolean writeSerialPortBytes(int [] bytes) throws SerialPortException {
        byte[] actuallyBytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            actuallyBytes[i] = ByteToolbox.toByte(bytes[i]);
        }
        boolean bytesSuccessfullyWritten = serialPort.writeBytes(actuallyBytes);
        return bytesSuccessfullyWritten;
    }

    public int[] readSerialPortBytes(int length, boolean bufferEnd, int timeout) throws SerialPortTimeoutException, SerialPortException {
        int[] serialPortData = serialPort.readIntArray(length, timeout);
        if(serialPort.getInputBufferBytesCount() > 0 && bufferEnd == true) {
            logger.error("Error: There are bytes left on " + serialPort.getPortName() + "; Bytes are " + Arrays.toString(serialPort.readBytes())); // flushes port
            throw new SerialPortException(serialPort.getPortName(), "readSerialPortBytes", "Bytes left on serial port after the read");
        } else {
            return serialPortData;
        }
    }

    public void changeBaud(int baudRate) throws SerialPortException {
        logger.debug("Changing baud rate to " + baudRate);
        this.baudRate = baudRate;
        serialPort.setParams(baudRate,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                parity);
    }

    public void flushBytes() throws SerialPortException {
        int[] flushedBytes = serialPort.readIntArray();
        logger.debug("Flushing Bytes: " + Arrays.toString(flushedBytes));
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    public int getBaudRate() {
        return baudRate;
    }
}