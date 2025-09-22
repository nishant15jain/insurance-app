package com.example.insurance_app.payments;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Management", description = "APIs for managing premium payments and payment history")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping
    @Operation(summary = "Process a payment", description = "Record a new payment for a user policy. Premium amount is automatically calculated from the policy if not provided.")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<PaymentDto> processPayment(@Valid @RequestBody PaymentCreateRequest request) {
        PaymentDto payment = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get payment history", description = "Get payment history for a specific user")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and #userId == authentication.principal.userId) or (hasRole('AGENT') and @userService.isUserAssignedToAgent(#userId, authentication.principal.userId))")
    public ResponseEntity<List<PaymentDto>> getPaymentHistory(
            @Parameter(description = "User ID") @PathVariable Long userId) {        
        List<PaymentDto> paymentHistory = paymentService.getPaymentHistory(userId);
        return ResponseEntity.ok(paymentHistory);
    }
    
    @GetMapping("/user-policy/{userPolicyId}")
    @Operation(summary = "Get payment history by user policy", description = "Get payment history for a specific user policy")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT') or @userPolicyService.isUserPolicyOwner(#userPolicyId, authentication.principal.userId)")
    public ResponseEntity<List<PaymentDto>> getPaymentHistoryByUserPolicy(
            @Parameter(description = "User Policy ID") @PathVariable Long userPolicyId) {
        List<PaymentDto> paymentHistory = paymentService.getPaymentHistoryByUserPolicy(userPolicyId);
        return ResponseEntity.ok(paymentHistory);
    }
    
    @GetMapping("/overdue")
    @Operation(summary = "Get overdue payments", description = "Admin endpoint to get all overdue payments")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<List<PaymentDto>> getOverduePayments() {
        List<PaymentDto> overduePayments = paymentService.getOverduePayments();
        return ResponseEntity.ok(overduePayments);
    }
    
    @GetMapping("/due-in-days/{days}")
    @Operation(summary = "Get payments due in next N days", description = "Get payments due in the next specified number of days")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<List<PaymentDto>> getPaymentsDueInDays(
            @Parameter(description = "Number of days to look ahead") @PathVariable int days) {        
        List<PaymentDto> paymentsDue = paymentService.getPaymentsDueInDays(days);
        return ResponseEntity.ok(paymentsDue);
    }
    
    @PutMapping("/{paymentId}/mark-paid")
    @Operation(summary = "Mark payment as paid", description = "Mark a pending payment as successfully paid")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<PaymentDto> markPaymentAsPaid(
            @Parameter(description = "Payment ID") @PathVariable Long paymentId,
            @Parameter(description = "Transaction ID") @RequestParam(required = false) String transactionId) {
        PaymentDto payment = paymentService.markPaymentAsPaid(paymentId, transactionId);
        return ResponseEntity.ok(payment);
    }
    
    @PutMapping("/{paymentId}/calculate-late-fee")
    @Operation(summary = "Calculate late fee", description = "Calculate and apply late fee to an overdue payment")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<PaymentDto> calculateLateFee(
            @Parameter(description = "Payment ID") @PathVariable Long paymentId) {
        PaymentDto payment = paymentService.calculateLateFee(paymentId);
        return ResponseEntity.ok(payment);
    }
    
    @PostMapping("/check-overdue")
    @Operation(summary = "Check overdue payments", description = "Admin endpoint to manually trigger overdue payment check")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentDto>> checkOverduePayments() {
        List<PaymentDto> overduePayments = paymentService.checkOverduePayments();
        return ResponseEntity.ok(overduePayments);
    }
}
