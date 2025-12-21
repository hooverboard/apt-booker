package com.aptBooker.backend.exceptions;

public class TimeslotNotAvailableException extends RuntimeException {
    public TimeslotNotAvailableException(String message) {
        super(message);
    }
}
