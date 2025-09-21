package com.example.insurance_app.policies;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyUpdateRequest {
    
    @Size(max = 50, message = "Policy number cannot exceed 50 characters")
    private String policyNumber;
    
    private Policy.PolicyType type;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    @Positive(message = "Coverage amount must be positive")
    private BigDecimal coverageAmount;
    
    @Positive(message = "Premium amount must be positive")
    private BigDecimal premiumAmount;
    
    @Positive(message = "Term years must be positive")
    private Integer termYears;
    
    private Policy.PremiumFrequency premiumFrequency;
}
