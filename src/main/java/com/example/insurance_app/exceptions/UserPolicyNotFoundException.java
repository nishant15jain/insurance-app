package com.example.insurance_app.exceptions;

public class UserPolicyNotFoundException extends RuntimeException {
    
    public UserPolicyNotFoundException(String message) {
        super(message);
    }
    
    public UserPolicyNotFoundException(Long userPolicyId) {
        super("User policy not found with ID: " + userPolicyId);
    }
    
    public UserPolicyNotFoundException(Long userId, Long policyId) {
        super("User policy not found for user ID: " + userId + " and policy ID: " + policyId);
    }
}
