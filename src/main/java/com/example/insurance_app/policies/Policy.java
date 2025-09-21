package com.example.insurance_app.policies;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "policies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Policy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Policy number is required")
    @Column(name = "policy_number", nullable = false, unique = true, length = 50)
    private String policyNumber;
    
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Policy type is required")
    @Column(name = "type", nullable = false)
    private PolicyType type;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @NotNull(message = "Coverage amount is required")
    @Positive(message = "Coverage amount must be positive")
    @Column(name = "coverage_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal coverageAmount;
    
    @NotNull(message = "Premium amount is required")
    @Positive(message = "Premium amount must be positive")
    @Column(name = "premium_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal premiumAmount;
    
    @NotNull(message = "Term years is required")
    @Positive(message = "Term years must be positive")
    @Column(name = "term_years", nullable = false)
    private Integer termYears;
    
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Premium frequency is required")
    @Column(name = "premium_frequency", nullable = false)
    private PremiumFrequency premiumFrequency = PremiumFrequency.ANNUAL;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum PolicyType {
        HEALTH("Health Insurance"),
        LIFE("Life Insurance"),
        VEHICLE("Vehicle Insurance"),
        TRAVEL("Travel Insurance");
        
        private final String displayName;
        
        PolicyType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum PremiumFrequency {
        MONTHLY("Monthly", 12),
        QUARTERLY("Quarterly", 4), 
        HALF_YEARLY("Half-Yearly", 2),
        ANNUAL("Annual", 1);
        
        private final String displayName;
        private final int paymentsPerYear;
        
        PremiumFrequency(String displayName, int paymentsPerYear) {
            this.displayName = displayName;
            this.paymentsPerYear = paymentsPerYear;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public int getPaymentsPerYear() {
            return paymentsPerYear;
        }
        
        public int getMonthsBetweenPayments() {
            return 12 / paymentsPerYear;
        }
    }
}
