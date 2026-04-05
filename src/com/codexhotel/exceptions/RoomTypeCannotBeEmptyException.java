package com.codexhotel.exceptions;

public class RoomTypeCannotBeEmptyException extends RuntimeException {

    public RoomTypeCannotBeEmptyException(String message) {
        super(message);
    }
}
