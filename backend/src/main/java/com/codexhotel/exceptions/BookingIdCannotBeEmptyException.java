package com.codexhotel.exceptions;

public class BookingIdCannotBeEmptyException extends RuntimeException {

    public BookingIdCannotBeEmptyException(String message) {
        super(message);
    }
}
