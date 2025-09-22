package com.example.insurance_app.claims;

import com.example.insurance_app.user_policies.UserPolicyDto;
import com.example.insurance_app.users.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDto {
    
    private Long id;
    private UserPolicyDto userPolicy;
    private BigDecimal claimAmount;
    private LocalDateTime claimDate;
    private Claim.ClaimStatus status;
    private String description;
    private UserDto processedBy;
}
