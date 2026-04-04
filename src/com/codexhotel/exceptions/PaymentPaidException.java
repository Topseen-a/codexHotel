package com.codexhotel.exceptions;

public class PaymentPaidException extends RuntimeException {

    public PaymentPaidException(String message) {
        super(message);
    }
}
