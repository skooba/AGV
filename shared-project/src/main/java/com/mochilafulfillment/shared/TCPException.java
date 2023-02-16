package com.mochilafulfillment.shared;

public class TCPException extends RuntimeException{
    public TCPException(String message) {
        super(message);
    }

    public TCPException(String message, Throwable cause) {
        super(message, cause);
    }

    public TCPException(Throwable cause) {
        super(cause);
    }
}
