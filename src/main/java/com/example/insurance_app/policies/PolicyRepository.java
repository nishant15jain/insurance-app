package com.example.insurance_app.policies;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    
    // Find policy by policy number
    Optional<Policy> findByPolicyNumber(String policyNumber);
    
    // Check if policy number exists
    boolean existsByPolicyNumber(String policyNumber);
    
    // Find policies by type
    List<Policy> findByType(Policy.PolicyType type);
    
    // Find policies with premium amount between min and max
    @Query("SELECT p FROM Policy p WHERE p.premiumAmount BETWEEN :minPremium AND :maxPremium")
    List<Policy> findByPremiumAmountBetween(@Param("minPremium") BigDecimal minPremium, 
                                           @Param("maxPremium") BigDecimal maxPremium);
    
    // Find policies with coverage amount greater than specified amount
    List<Policy> findByCoverageAmountGreaterThan(BigDecimal minCoverage);
    
    // Find policies by term years
    List<Policy> findByTermYears(Integer termYears);
    
    // Find policies with term years between min and max
    List<Policy> findByTermYearsBetween(Integer minYears, Integer maxYears);
    
    // Search policies by description containing keyword (case insensitive)
    @Query("SELECT p FROM Policy p WHERE LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Policy> findByDescriptionContainingIgnoreCase(@Param("keyword") String keyword);
    
    // Find all policies ordered by creation date (newest first)
    @Query("SELECT p FROM Policy p ORDER BY p.createdAt DESC")
    List<Policy> findAllOrderByCreatedAtDesc();
    
    // Find policies by type ordered by premium amount
    List<Policy> findByTypeOrderByPremiumAmountAsc(Policy.PolicyType type);
}
