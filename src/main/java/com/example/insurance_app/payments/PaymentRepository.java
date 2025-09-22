package com.example.insurance_app.payments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    // Find payments by user policy ID
    List<Payment> findByUserPolicyId(Long userPolicyId);
    
    // Find payments by user policy ID ordered by payment date desc
    List<Payment> findByUserPolicyIdOrderByPaymentDateDesc(Long userPolicyId);
    
    // Find overdue payments (status = PENDING and due_date < current date)
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.dueDate < :currentDate")
    List<Payment> findOverduePayments(@Param("currentDate") LocalDate currentDate);
    
    // Find payments by user ID (through user policy relationship)
    @Query("SELECT p FROM Payment p JOIN p.userPolicy up WHERE up.user.id = :userId ORDER BY p.paymentDate DESC")
    List<Payment> findByUserId(@Param("userId") Long userId);
    
    // Find payments by user ID and payment type
    @Query("SELECT p FROM Payment p JOIN p.userPolicy up WHERE up.user.id = :userId AND p.paymentType = :paymentType ORDER BY p.paymentDate DESC")
    List<Payment> findByUserIdAndPaymentType(@Param("userId") Long userId, @Param("paymentType") Payment.PaymentType paymentType);
    
    // Find payment by transaction ID
    Optional<Payment> findByTransactionId(String transactionId);
    
    // Find payments due in the next N days
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.dueDate BETWEEN :today AND :futureDate")
    List<Payment> findPaymentsDueInRange(@Param("today") LocalDate today, @Param("futureDate") LocalDate futureDate);
    
    // Find payments by status
    List<Payment> findByStatus(Payment.PaymentStatus status);
    
    // Find payments by user policy and status
    List<Payment> findByUserPolicyIdAndStatus(Long userPolicyId, Payment.PaymentStatus status);
    
    // Count overdue payments for a user
    @Query("SELECT COUNT(p) FROM Payment p JOIN p.userPolicy up WHERE up.user.id = :userId AND p.status = 'PENDING' AND p.dueDate < :currentDate")
    long countOverduePaymentsByUserId(@Param("userId") Long userId, @Param("currentDate") LocalDate currentDate);
    
    // Find latest payment for a user policy
    @Query("SELECT p FROM Payment p WHERE p.userPolicy.id = :userPolicyId ORDER BY p.paymentDate DESC LIMIT 1")
    Optional<Payment> findLatestPaymentByUserPolicyId(@Param("userPolicyId") Long userPolicyId);
}
