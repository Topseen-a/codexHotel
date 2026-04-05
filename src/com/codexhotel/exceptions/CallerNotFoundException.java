package com.codexhotel.exceptions;

public class CallerNotFoundException extends RuntimeException {

    public CallerNotFoundException(String message) {
        super(message);
    }
}
