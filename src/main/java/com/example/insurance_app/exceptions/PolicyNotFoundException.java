package com.example.insurance_app.exceptions;

public class PolicyNotFoundException extends RuntimeException {
    
    public PolicyNotFoundException(String message) {
        super(message);
    }
    
    public PolicyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static PolicyNotFoundException byId(Long id) {
        return new PolicyNotFoundException("Policy not found with id: " + id);
    }
    
    public static PolicyNotFoundException byPolicyNumber(String policyNumber) {
        return new PolicyNotFoundException("Policy not found with policy number: " + policyNumber);
    }
}
