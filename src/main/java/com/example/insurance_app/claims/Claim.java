package com.example.insurance_app.claims;

import com.example.insurance_app.user_policies.UserPolicy;
import com.example.insurance_app.users.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Claim {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_policy_id", nullable = false)
    @NotNull(message = "User policy is required")
    private UserPolicy userPolicy;
    
    @NotNull(message = "Claim amount is required")
    @Column(name = "claim_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal claimAmount;
    
    @CreationTimestamp
    @Column(name = "claim_date", updatable = false)
    private LocalDateTime claimDate;
    
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required")
    @Column(name = "status", nullable = false)
    private ClaimStatus status = ClaimStatus.PENDING;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(name = "description", length = 1000)
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by")
    private User processedBy;
    
    public enum ClaimStatus {
        PENDING("Pending"),
        APPROVED("Approved"),
        REJECTED("Rejected"),
        PAID("Paid");
        
        private final String displayName;
        
        ClaimStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Helper methods
    public boolean isPending() {
        return this.status == ClaimStatus.PENDING;
    }
    
    public boolean isApproved() {
        return this.status == ClaimStatus.APPROVED;
    }
    
    public boolean canBeProcessed() {
        return this.status == ClaimStatus.PENDING;
    }
}
