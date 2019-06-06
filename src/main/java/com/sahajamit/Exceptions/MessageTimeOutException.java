package com.sahajamit.Exceptions;

public class MessageTimeOutException extends Exception{
    public MessageTimeOutException() {
        super();
    }
    public MessageTimeOutException(String message) {
        super(message);
    }
    public MessageTimeOutException(String message, Throwable cause) {
        super(message,cause);
    }
}
