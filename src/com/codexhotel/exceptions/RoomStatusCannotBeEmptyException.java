package com.codexhotel.exceptions;

public class RoomStatusCannotBeEmptyException extends RuntimeException {

    public RoomStatusCannotBeEmptyException(String message) {
        super(message);
    }
}
