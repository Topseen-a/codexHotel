package com.codexhotel.exceptions;

public class RoomNumberAlreadyExistsException extends RuntimeException {

    public RoomNumberAlreadyExistsException(String message) {
        super(message);
    }
}
