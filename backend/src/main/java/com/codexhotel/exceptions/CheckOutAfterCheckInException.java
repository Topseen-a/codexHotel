package com.codexhotel.exceptions;

public class CheckOutAfterCheckInException extends RuntimeException {

    public CheckOutAfterCheckInException(String message) {
        super(message);
    }
}
