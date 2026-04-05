package com.codexhotel.exceptions;

public class InvalidRoomRequestException extends RuntimeException {

    public InvalidRoomRequestException(String message) {
        super(message);
    }
}
