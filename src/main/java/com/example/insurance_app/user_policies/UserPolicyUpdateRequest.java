package com.example.insurance_app.user_policies;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class UserPolicyUpdateRequest {
    
    // Note: Start date and end date are not updatable - they are fixed based on policy term
    private UserPolicy.Status status;
    private LocalDate nextPremiumDue;
    
    // Constructor for status updates only
    public UserPolicyUpdateRequest(UserPolicy.Status status) {
        this.status = status;
    }
    
    // Constructor for premium due date updates
    public UserPolicyUpdateRequest(LocalDate nextPremiumDue) {
        this.nextPremiumDue = nextPremiumDue;
    }
    
    // Constructor for status and premium due updates
    public UserPolicyUpdateRequest(UserPolicy.Status status, LocalDate nextPremiumDue) {
        this.status = status;
        this.nextPremiumDue = nextPremiumDue;
    }
}
