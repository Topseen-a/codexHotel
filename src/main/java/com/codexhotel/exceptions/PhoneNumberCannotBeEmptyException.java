package com.codexhotel.exceptions;

public class PhoneNumberCannotBeEmptyException extends RuntimeException {

    public PhoneNumberCannotBeEmptyException(String message) {
        super(message);
    }
}
