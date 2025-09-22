package com.example.insurance_app.claims;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimCreateRequest {
    
    @NotNull(message = "User policy ID is required")
    private Long userPolicyId;
    
    @NotNull(message = "Claim amount is required")
    @DecimalMin(value = "0.01", message = "Claim amount must be greater than 0")
    private BigDecimal claimAmount;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
}
