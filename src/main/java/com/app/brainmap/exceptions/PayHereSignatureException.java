package com.app.brainmap.exceptions;

public class PayHereSignatureException extends PaymentException {
    
    public PayHereSignatureException(String message) {
        super(message);
    }
    
    public PayHereSignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
