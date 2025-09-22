package com.example.insurance_app.claims;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    
    // Find claims by user ID
    @Query("SELECT c FROM Claim c JOIN c.userPolicy up WHERE up.user.id = :userId")
    List<Claim> findByUserId(@Param("userId") Long userId);
    
    // Find claims by user policy ID
    List<Claim> findByUserPolicyId(Long userPolicyId);
    
    // Find claims by status
    List<Claim> findByStatus(Claim.ClaimStatus status);
    
    // Find pending claims for admin/agent review
    List<Claim> findByStatusOrderByClaimDateAsc(Claim.ClaimStatus status);
    
    // Count claims by user
    @Query("SELECT COUNT(c) FROM Claim c JOIN c.userPolicy up WHERE up.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}
