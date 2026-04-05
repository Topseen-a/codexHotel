package com.codexhotel.exceptions;

public class UserIdCannotBeEmptyException extends RuntimeException {

    public UserIdCannotBeEmptyException(String message) {
        super(message);
    }
}
