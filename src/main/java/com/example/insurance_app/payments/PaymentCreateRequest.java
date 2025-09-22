package com.example.insurance_app.payments;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.insurance_app.payments.Payment.PaymentType;
import com.example.insurance_app.payments.Payment.PaymentMethod;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateRequest {
    
    @NotNull(message = "User policy ID is required")
    private Long userPolicyId;
    
    // Amount is optional - if not provided, it will be calculated from the policy
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotNull(message = "Due date is required")
    private LocalDate dueDate;
    
    private PaymentType paymentType = PaymentType.PREMIUM;
    
    private PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
    
    private String notes;
    
    // For processing immediate payments
    private String transactionId;
    
    // Constructor for premium payments (with explicit amount)
    public PaymentCreateRequest(Long userPolicyId, BigDecimal amount, LocalDate dueDate) {
        this.userPolicyId = userPolicyId;
        this.amount = amount;
        this.dueDate = dueDate;
        this.paymentType = PaymentType.PREMIUM;
    }
    
    // Constructor for premium payments (amount will be calculated from policy)
    public PaymentCreateRequest(Long userPolicyId, LocalDate dueDate) {
        this.userPolicyId = userPolicyId;
        this.dueDate = dueDate;
        this.paymentType = PaymentType.PREMIUM;
        // amount will be null and calculated automatically
    }
}
