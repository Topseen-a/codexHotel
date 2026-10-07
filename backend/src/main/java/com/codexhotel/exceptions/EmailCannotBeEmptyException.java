package com.codexhotel.exceptions;

public class EmailCannotBeEmptyException extends RuntimeException {

    public EmailCannotBeEmptyException(String message) {
        super(message);
    }
}
