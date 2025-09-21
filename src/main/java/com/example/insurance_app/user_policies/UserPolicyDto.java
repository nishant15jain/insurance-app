package com.example.insurance_app.user_policies;

import com.example.insurance_app.policies.PolicyDto;
import com.example.insurance_app.users.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPolicyDto {
    
    private Long id;
    
    private UserDto user;
    
    private PolicyDto policy;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private UserPolicy.Status status;
    
    private LocalDate nextPremiumDue;
    
    private LocalDateTime createdAt;
    
    // Additional computed fields for convenience
    private boolean isActive;
    
    private boolean isExpired;
    
    private boolean isPremiumDue;
    
    private long daysUntilExpiry;
    
    private long daysUntilPremiumDue;
    
    // Simplified constructor for basic info
    public UserPolicyDto(Long id, Long userId, String userName, String userEmail,
                        Long policyId, String policyNumber, String policyType,
                        LocalDate startDate, LocalDate endDate, UserPolicy.Status status,
                        LocalDate nextPremiumDue, LocalDateTime createdAt) {
        this.id = id;
        this.user = new UserDto();
        this.user.setId(userId);
        this.user.setName(userName);
        this.user.setEmail(userEmail);
        
        this.policy = new PolicyDto();
        this.policy.setId(policyId);
        this.policy.setPolicyNumber(policyNumber);
        
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.nextPremiumDue = nextPremiumDue;
        this.createdAt = createdAt;
        
        // Calculate computed fields
        this.isActive = status == UserPolicy.Status.ACTIVE;
        this.isExpired = endDate.isBefore(LocalDate.now());
        this.isPremiumDue = nextPremiumDue != null && nextPremiumDue.isBefore(LocalDate.now().plusDays(1));
        this.daysUntilExpiry = LocalDate.now().until(endDate).getDays();
        this.daysUntilPremiumDue = nextPremiumDue != null ? 
            java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), nextPremiumDue) : -1;
    }
}
