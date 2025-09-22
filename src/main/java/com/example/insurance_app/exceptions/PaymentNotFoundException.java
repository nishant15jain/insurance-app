package com.example.insurance_app.exceptions;

public class PaymentNotFoundException extends RuntimeException {
    
    public PaymentNotFoundException(Long paymentId) {
        super("Payment not found with ID: " + paymentId);
    }
    
    public PaymentNotFoundException(String message) {
        super(message);
    }
    
    public PaymentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
