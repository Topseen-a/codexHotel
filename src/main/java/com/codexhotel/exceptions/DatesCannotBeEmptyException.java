package com.codexhotel.exceptions;

public class DatesCannotBeEmptyException extends RuntimeException {

    public DatesCannotBeEmptyException(String message) {
        super(message);
    }
}
