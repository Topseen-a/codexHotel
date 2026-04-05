package com.codexhotel.exceptions;

public class RoomIdCannotBeEmptyException extends RuntimeException {

    public RoomIdCannotBeEmptyException(String message) {
        super(message);
    }
}
