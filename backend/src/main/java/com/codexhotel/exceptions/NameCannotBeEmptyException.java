package com.codexhotel.exceptions;

public class NameCannotBeEmptyException extends RuntimeException {

    public NameCannotBeEmptyException(String message) {
        super(message);
    }
}
