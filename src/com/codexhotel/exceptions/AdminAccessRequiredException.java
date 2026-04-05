package com.codexhotel.exceptions;

public class AdminAccessRequiredException extends RuntimeException {

    public AdminAccessRequiredException(String message) {
        super(message);
    }
}
