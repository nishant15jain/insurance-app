package com.example.insurance_app.exceptions;

public class PolicyAlreadyExistsException extends RuntimeException {
    
    public PolicyAlreadyExistsException(String message) {
        super(message);
    }
    
    public PolicyAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static PolicyAlreadyExistsException withPolicyNumber(String policyNumber) {
        return new PolicyAlreadyExistsException("Policy already exists with policy number: " + policyNumber);
    }
}
