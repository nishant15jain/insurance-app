package com.example.insurance_app.user_policies;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class UserPolicyCreateRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Policy ID is required")
    private Long policyId;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    // Constructor for quick policy purchase with default dates
    public UserPolicyCreateRequest(Long userId, Long policyId) {
        this.userId = userId;
        this.policyId = policyId;
        this.startDate = LocalDate.now();
    }
    
    // Constructor with custom start date
    public UserPolicyCreateRequest(Long userId, Long policyId, LocalDate startDate) {
        this.userId = userId;
        this.policyId = policyId;
        this.startDate = startDate;
    }
}
