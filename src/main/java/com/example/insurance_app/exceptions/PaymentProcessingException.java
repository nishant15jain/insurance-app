package com.example.insurance_app.exceptions;

public class PaymentProcessingException extends RuntimeException {
    
    public PaymentProcessingException(String message) {
        super(message);
    }
    
    public PaymentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static PaymentProcessingException invalidAmount(String details) {
        return new PaymentProcessingException("Invalid payment amount: " + details);
    }
    
    public static PaymentProcessingException paymentFailed(String reason) {
        return new PaymentProcessingException("Payment processing failed: " + reason);
    }
    
    public static PaymentProcessingException invalidStatus(String currentStatus, String operation) {
        return new PaymentProcessingException(
            String.format("Cannot perform %s on payment with status: %s", operation, currentStatus));
    }
}
