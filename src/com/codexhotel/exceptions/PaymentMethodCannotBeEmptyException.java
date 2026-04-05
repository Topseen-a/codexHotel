package com.codexhotel.exceptions;

public class PaymentMethodCannotBeEmptyException extends RuntimeException {

    public PaymentMethodCannotBeEmptyException(String message) {
        super(message);
    }
}
