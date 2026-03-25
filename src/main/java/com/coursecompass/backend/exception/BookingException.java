package com.coursecompass.backend.exception;

/**
 * Custom exception for booking-related errors
 */
public class BookingException extends RuntimeException {

    private final String errorCode;

    public BookingException(String message) {
        super(message);
        this.errorCode = "BOOKING_ERROR";
    }

    public BookingException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BookingException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BOOKING_ERROR";
    }

    public String getErrorCode() {
        return errorCode;
    }
}