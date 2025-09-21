package com.example.insurance_app.user_policies;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.insurance_app.policies.Policy;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserPolicyRepository extends JpaRepository<UserPolicy, Long> {
    
    // Find all policies for a specific user
    List<UserPolicy> findByUserId(Long userId);
    
    // Find all user policies for a specific policy    
    List<UserPolicy> findByPolicyId(Long policyId);
    
    // Find user policies by user ID and status
    List<UserPolicy> findByUserIdAndStatus(Long userId, UserPolicy.Status status);
    
    // Find specific user-policy mapping
    Optional<UserPolicy> findByUserIdAndPolicyId(Long userId, Long policyId);
    
    // Find all policies by status
    List<UserPolicy> findByStatus(UserPolicy.Status status);
    
    // Find policies with premium due before specified date
    List<UserPolicy> findByNextPremiumDueBefore(LocalDate date);
    
    // Find active policies for a user
    @Query("SELECT up FROM UserPolicy up WHERE up.user.id = :userId AND up.status = 'ACTIVE'")
    List<UserPolicy> findActiveByUserId(@Param("userId") Long userId);
    
    // Find policies expiring within specified days
    @Query("SELECT up FROM UserPolicy up WHERE up.endDate BETWEEN :startDate AND :endDate AND up.status = 'ACTIVE'")
    List<UserPolicy> findPoliciesExpiringBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Check if user already has an active policy of the same type
    @Query("SELECT COUNT(up) > 0 FROM UserPolicy up WHERE up.user.id = :userId AND up.policy.id = :policyId AND up.status = 'ACTIVE'")
    boolean existsActiveUserPolicy(@Param("userId") Long userId, @Param("policyId") Long policyId);
    
    // Find all user policies with user and policy details (optimized query)
    @Query("SELECT up FROM UserPolicy up JOIN FETCH up.user JOIN FETCH up.policy WHERE up.user.id = :userId")
    List<UserPolicy> findByUserIdWithDetails(@Param("userId") Long userId);
    
    // Find user policies by policy type
    @Query("SELECT up FROM UserPolicy up WHERE up.user.id = :userId AND up.policy.type = :policyType")
    List<UserPolicy> findByUserIdAndPolicyType(
            @Param("userId") Long userId, 
            @Param("policyType") Policy.PolicyType policyType);
    
    
    // Count active policies for a user
    @Query("SELECT COUNT(up) FROM UserPolicy up WHERE up.user.id = :userId AND up.status = 'ACTIVE'")
    long countActiveByUserId(@Param("userId") Long userId);
    
    // Find policies due for renewal (premium due in next N days)
    @Query("SELECT up FROM UserPolicy up WHERE up.nextPremiumDue BETWEEN :today AND :dueDate AND up.status = 'ACTIVE'")
    List<UserPolicy> findPoliciesDueForRenewal(@Param("today") LocalDate today, @Param("dueDate") LocalDate dueDate);
}
