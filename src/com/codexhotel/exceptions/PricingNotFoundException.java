package com.codexhotel.exceptions;

public class PricingNotFoundException extends RuntimeException {

    public PricingNotFoundException(String message) {
        super(message);
    }
}
