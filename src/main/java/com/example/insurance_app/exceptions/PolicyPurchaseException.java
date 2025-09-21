package com.example.insurance_app.exceptions;

public class PolicyPurchaseException extends RuntimeException {
    
    public PolicyPurchaseException(String message) {
        super(message);
    }
    
    public PolicyPurchaseException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static PolicyPurchaseException userAlreadyHasPolicy(Long userId, Long policyId) {
        return new PolicyPurchaseException("User " + userId + " already has an active policy " + policyId);
    }
    
    public static PolicyPurchaseException invalidDates(String reason) {
        return new PolicyPurchaseException("Invalid policy dates: " + reason);
    }
    
    public static PolicyPurchaseException policyNotAvailable(Long policyId) {
        return new PolicyPurchaseException("Policy " + policyId + " is not available for purchase");
    }
}
