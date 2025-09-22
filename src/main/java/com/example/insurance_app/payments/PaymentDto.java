package com.example.insurance_app.payments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    
    private Long id;
    private Long userPolicyId;
    private String policyNumber;
    private String userName;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private Payment.PaymentStatus status;
    private String transactionId;
    private Payment.PaymentType paymentType;
    private LocalDate dueDate;
    private Payment.PaymentMethod paymentMethod;
    private BigDecimal lateFeeAmount;
    private String notes;
    private LocalDateTime updatedAt;
    
    // Computed fields
    private boolean overdue;
    private long daysOverdue;
    private BigDecimal totalAmount;
    
    // Constructor for basic payment info
    public PaymentDto(Long id, Long userPolicyId, BigDecimal amount, 
                     LocalDateTime paymentDate, Payment.PaymentStatus status, 
                     LocalDate dueDate, Payment.PaymentType paymentType) {
        this.id = id;
        this.userPolicyId = userPolicyId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.status = status;
        this.dueDate = dueDate;
        this.paymentType = paymentType;
        
        // Calculate computed fields
        this.overdue = status == Payment.PaymentStatus.PENDING && 
                        dueDate.isBefore(LocalDate.now());
        this.daysOverdue = overdue ? 
            java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now()) : 0;
        this.totalAmount = amount.add(lateFeeAmount != null ? lateFeeAmount : BigDecimal.ZERO);
    }
}
