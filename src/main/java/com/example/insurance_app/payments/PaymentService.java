package com.example.insurance_app.payments;

import com.example.insurance_app.exceptions.PolicyNotFoundException;
import com.example.insurance_app.user_policies.UserPolicy;
import com.example.insurance_app.user_policies.UserPolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final UserPolicyRepository userPolicyRepository;
    private final PaymentMapper paymentMapper;
    
    // Late fee configuration
    private static final BigDecimal LATE_FEE_PERCENTAGE = new BigDecimal("0.05"); // 5%
    private static final BigDecimal MAX_LATE_FEE = new BigDecimal("1000.00");
    private static final int GRACE_PERIOD_DAYS = 15;
    
    // Process a payment - record payment and update status
    public PaymentDto processPayment(PaymentCreateRequest request) {
        log.info("Processing payment for user policy ID: {}", request.getUserPolicyId());
        UserPolicy userPolicy = userPolicyRepository.findById(request.getUserPolicyId())
            .orElseThrow(() -> new PolicyNotFoundException("User policy not found with ID: " + request.getUserPolicyId()));
        
        // Validate due date matches the user policy's next premium due date
        if (request.getPaymentType() == Payment.PaymentType.PREMIUM) {
            validatePaymentDueDate(request.getDueDate(), userPolicy.getNextPremiumDue());
        }
        
        // If amount is not provided, calculate it from the policy
        if (request.getAmount() == null) {
            BigDecimal premiumAmount = calculatePremiumAmount(userPolicy);
            request.setAmount(premiumAmount);
        }
        Payment payment = paymentMapper.toEntity(request);
        payment.setUserPolicy(userPolicy);
        if (request.getTransactionId() == null || request.getTransactionId().isEmpty()) {
            payment.setTransactionId(generateTransactionId());
        } else {
            payment.setTransactionId(request.getTransactionId());
        }
        
        // If money is paid (transactionId provided), mark as SUCCESS. DueDate only determines when payment becomes overdue, not when payment is valid
        if (payment.getTransactionId() != null && !payment.getTransactionId().isEmpty()) {
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            updateNextPremiumDue(userPolicy);  // Update user policy next premium due date
            activatePolicyIfPending(userPolicy);  // Activate policy if it's in pending status
        }
        
        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toDto(savedPayment);
    }
    
    // Get payment history for a user
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentHistory(Long userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);
        return paymentMapper.toDtoList(payments);
    }
    
    // Get payment history for a specific user policy
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentHistoryByUserPolicy(Long userPolicyId) {        
        List<Payment> payments = paymentRepository.findByUserPolicyIdOrderByPaymentDateDesc(userPolicyId);
        return paymentMapper.toDtoList(payments);
    }
    
    // Check and mark overdue payments
    public List<PaymentDto> checkOverduePayments() {
        LocalDate currentDate = LocalDate.now().minusDays(GRACE_PERIOD_DAYS);
        List<Payment> overduePayments = paymentRepository.findOverduePayments(currentDate);
        log.info("Found {} overdue payments", overduePayments.size());
        for (Payment payment : overduePayments) {
            calculateLateFee(payment.getId());
            UserPolicy userPolicy = payment.getUserPolicy();
            if (userPolicy.getStatus() == UserPolicy.Status.ACTIVE) {
                // Check if policy should be marked as LAPSED
                long overdueCount = paymentRepository.countOverduePaymentsByUserId(
                    userPolicy.getUser().getId(), LocalDate.now());
                
                if (overdueCount >= 2) { // Policy lapses after 2 overdue payments
                    userPolicy.setStatus(UserPolicy.Status.LAPSED);
                    userPolicyRepository.save(userPolicy);
                    log.info("User policy {} marked as LAPSED due to overdue payments", userPolicy.getId());
                }
            }
        }
        
        return paymentMapper.toDtoList(overduePayments);
    }
    
    // Calculate and apply late fee to a payment
    public PaymentDto calculateLateFee(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));
        
        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            log.warn("Cannot apply late fee to non-pending payment: {}", paymentId);
            return paymentMapper.toDto(payment);
        }
        if (!payment.isOverdue()) {
            log.warn("Payment is not overdue, no late fee applied: {}", paymentId);
            return paymentMapper.toDto(payment);
        }
        
        // Calculate late fee as percentage of original amount
        BigDecimal lateFee = payment.getAmount()
            .multiply(LATE_FEE_PERCENTAGE)
            .setScale(2, RoundingMode.HALF_UP);
        if (lateFee.compareTo(MAX_LATE_FEE) > 0) {
            lateFee = MAX_LATE_FEE;
        }
        payment.setLateFeeAmount(lateFee);
        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toDto(savedPayment);
    }
    
    // Get all overdue payments (admin function)
    @Transactional(readOnly = true)
    public List<PaymentDto> getOverduePayments() {
        List<Payment> overduePayments = paymentRepository.findOverduePayments(LocalDate.now());
        return paymentMapper.toDtoList(overduePayments);
    }
    
    // Mark payment as paid
    public PaymentDto markPaymentAsPaid(Long paymentId, String transactionId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));
        
        payment.setStatus(Payment.PaymentStatus.SUCCESS);
        if (transactionId != null && !transactionId.isEmpty()) {
            payment.setTransactionId(transactionId);
        }
        
        Payment savedPayment = paymentRepository.save(payment);
        updateNextPremiumDue(payment.getUserPolicy());
        activatePolicyIfPending(payment.getUserPolicy());  // Activate policy if it's in pending status
        return paymentMapper.toDto(savedPayment);
    }
        
    // Get payments due in the next N days
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsDueInDays(int days) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);
        
        List<Payment> paymentsDue = paymentRepository.findPaymentsDueInRange(today, futureDate);
        return paymentMapper.toDtoList(paymentsDue);
    }
    
    // Private helper methods
    private String generateTransactionId() {
        return "TXN_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
    
    private void updateNextPremiumDue(UserPolicy userPolicy) {
        // Calculate next premium due date based on policy frequency from current due date
        int monthsBetweenPayments = userPolicy.getPolicy().getPremiumFrequency().getMonthsBetweenPayments();
        LocalDate currentDueDate = userPolicy.getNextPremiumDue();
        if (currentDueDate == null) {
            currentDueDate = LocalDate.now();
        }
        
        LocalDate nextDueDate = currentDueDate.plusMonths(monthsBetweenPayments);
        userPolicy.setNextPremiumDue(nextDueDate);
        userPolicyRepository.save(userPolicy);
    }
    
    private void validatePaymentDueDate(LocalDate requestedDueDate, LocalDate expectedDueDate) {
        if (expectedDueDate == null) {
            throw new IllegalArgumentException("User policy does not have a next premium due date set");
        }
        
        if (!requestedDueDate.equals(expectedDueDate)) {
            throw new IllegalArgumentException(
                String.format("Payment due date %s does not match expected due date %s", 
                    requestedDueDate, expectedDueDate));
        }
    }
    
    private BigDecimal calculatePremiumAmount(UserPolicy userPolicy) {
        BigDecimal annualPremium = userPolicy.getPolicy().getPremiumAmount();
        int paymentsPerYear = userPolicy.getPolicy().getPremiumFrequency().getPaymentsPerYear();
        
        return annualPremium.divide(BigDecimal.valueOf(paymentsPerYear), 2, RoundingMode.HALF_UP);
    }
    
    private void activatePolicyIfPending(UserPolicy userPolicy) {
        if (userPolicy.isPending()) {
            userPolicy.activatePolicy();
            userPolicyRepository.save(userPolicy);
            log.info("Policy {} successfully activated", userPolicy.getId());
        }
    }
}
