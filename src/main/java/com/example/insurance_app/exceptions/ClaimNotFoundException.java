package com.example.insurance_app.exceptions;

public class ClaimNotFoundException extends RuntimeException {
    
    public ClaimNotFoundException(Long claimId) {
        super("Claim not found with ID: " + claimId);
    }
    
    public ClaimNotFoundException(String message) {
        super(message);
    }
}
