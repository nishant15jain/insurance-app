package com.example.insurance_app.exceptions;

public class UnauthorizedAccessException extends RuntimeException {
    
    public UnauthorizedAccessException(String message) {
        super(message);
    }
    
    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static UnauthorizedAccessException forAction(String action) {
        return new UnauthorizedAccessException("Unauthorized access for action: " + action);
    }
    
    public static UnauthorizedAccessException forRole(String requiredRole) {
        return new UnauthorizedAccessException("Access denied. Required role: " + requiredRole);
    }
}
