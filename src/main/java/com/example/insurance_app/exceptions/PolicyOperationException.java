package com.example.insurance_app.exceptions;

public class PolicyOperationException extends RuntimeException {
    
    public PolicyOperationException(String message) {
        super(message);
    }
    
    public PolicyOperationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static PolicyOperationException cannotCancel(String reason) {
        return new PolicyOperationException("Cannot cancel policy: " + reason);
    }
    
    public static PolicyOperationException cannotRenew(String reason) {
        return new PolicyOperationException("Cannot renew policy: " + reason);
    }
    
    public static PolicyOperationException invalidStatusTransition(String from, String to) {
        return new PolicyOperationException("Invalid status transition from " + from + " to " + to);
    }
}
