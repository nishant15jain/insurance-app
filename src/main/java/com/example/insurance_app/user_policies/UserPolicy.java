package com.example.insurance_app.user_policies;

import com.example.insurance_app.policies.Policy;
import com.example.insurance_app.users.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_policies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPolicy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    @NotNull(message = "Policy is required")
    private Policy policy;
    
    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required")
    @Column(name = "status", nullable = false)
    private Status status = Status.ACTIVE;
    
    @Column(name = "next_premium_due")
    private LocalDate nextPremiumDue;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    public enum Status {
        ACTIVE("Active"),
        LAPSED("Lapsed"),
        CANCELLED("Cancelled");
        
        private final String displayName;
        
        Status(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Helper methods for business logic
    public boolean isActive() {
        return this.status == Status.ACTIVE;
    }
    
    public boolean isExpired() {
        return this.endDate.isBefore(LocalDate.now());
    }
    
    public boolean isPremiumDue() {
        return this.nextPremiumDue != null && 
               this.nextPremiumDue.isBefore(LocalDate.now().plusDays(1));
    }
}
