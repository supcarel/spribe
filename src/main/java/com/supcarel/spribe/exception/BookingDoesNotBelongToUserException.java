package com.supcarel.spribe.exception;

public class BookingDoesNotBelongToUserException extends RuntimeException {
    public BookingDoesNotBelongToUserException(String message) {
        super(message);
    }
}
