package com.example.insurance_app.policies;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDto {
    
    private Long id;
    private String policyNumber;
    private Policy.PolicyType type;
    private String typeDisplayName;
    private String description;
    private BigDecimal coverageAmount;
    private BigDecimal premiumAmount;
    private Integer termYears;
    private Policy.PremiumFrequency premiumFrequency;
    private String premiumFrequencyDisplayName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
