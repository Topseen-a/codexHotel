package com.codexhotel.exceptions;

public class AmountCannotBeLessThanZeroException extends RuntimeException {

    public AmountCannotBeLessThanZeroException(String message) {
        super(message);
    }
}
