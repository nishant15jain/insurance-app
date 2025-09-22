package com.example.insurance_app.claims;

import com.example.insurance_app.exceptions.ClaimNotFoundException;
import com.example.insurance_app.exceptions.PolicyOperationException;
import com.example.insurance_app.user_policies.UserPolicy;
import com.example.insurance_app.user_policies.UserPolicyRepository;
import com.example.insurance_app.users.User;
import com.example.insurance_app.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimService {
    
    private final ClaimRepository claimRepository;
    private final UserPolicyRepository userPolicyRepository;
    private final UserRepository userRepository;
    private final ClaimMapper claimMapper;
    
    // Submit a new claim
    @Transactional
    public ClaimDto submitClaim(ClaimCreateRequest request) {

        // Validate user policy exists and is active
        UserPolicy userPolicy = userPolicyRepository.findById(request.getUserPolicyId())
                .orElseThrow(() -> new PolicyOperationException("User policy not found with ID: " + request.getUserPolicyId()));
        
        if (!userPolicy.isActive()) {
            throw new PolicyOperationException("Cannot submit claim for inactive policy");
        }
        
        // Create claim entity
        Claim claim = claimMapper.toEntity(request);
        claim.setUserPolicy(userPolicy);
        claim.setStatus(Claim.ClaimStatus.PENDING);
        
        Claim savedClaim = claimRepository.save(claim);
        log.info("Claim submitted successfully with ID {}", savedClaim.getId());
        return claimMapper.toDto(savedClaim);
    }
    
    // Get claims for a specific user
    @Transactional(readOnly = true)
    public List<ClaimDto> getClaimsByUserId(Long userId) {
        List<Claim> claims = claimRepository.findByUserId(userId);
        return claimMapper.toDtoList(claims);
    }
    
    // Get claim by ID
    @Transactional(readOnly = true)
    public ClaimDto getClaimById(Long id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        return claimMapper.toDto(claim);
    }
    
    // Get all pending claims (for admin/agent review)
    @Transactional(readOnly = true)
    public List<ClaimDto> getPendingClaims() {
        List<Claim> pendingClaims = claimRepository.findByStatusOrderByClaimDateAsc(Claim.ClaimStatus.PENDING);
        return claimMapper.toDtoList(pendingClaims);
    }
    
    // Approve a claim
    @Transactional
    public ClaimDto approveClaim(Long claimId, Long processedByUserId) {        
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ClaimNotFoundException(claimId));
        
        if (!claim.canBeProcessed()) {
            throw new PolicyOperationException("Claim cannot be processed. Current status: " + claim.getStatus());
        }
        
        User processedBy = userRepository.findById(processedByUserId)
                .orElseThrow(() -> new PolicyOperationException("User not found with ID: " + processedByUserId));
        
        claim.setStatus(Claim.ClaimStatus.APPROVED);
        claim.setProcessedBy(processedBy);
        
        Claim updatedClaim = claimRepository.save(claim);
        return claimMapper.toDto(updatedClaim);
    }
    
    // Reject a claim
    @Transactional
    public ClaimDto rejectClaim(Long claimId, Long processedByUserId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ClaimNotFoundException(claimId));
        
        if (!claim.canBeProcessed()) {
            throw new PolicyOperationException("Claim cannot be processed. Current status: " + claim.getStatus());
        }
        
        User processedBy = userRepository.findById(processedByUserId)
                .orElseThrow(() -> new PolicyOperationException("User not found with ID: " + processedByUserId));
        
        claim.setStatus(Claim.ClaimStatus.REJECTED);
        claim.setProcessedBy(processedBy);
        
        Claim updatedClaim = claimRepository.save(claim);
        return claimMapper.toDto(updatedClaim);
    }
    
    // Check if user owns the claim (for security)
    @Transactional(readOnly = true)
    public boolean isClaimOwner(Long claimId, Long userId) {
        return claimRepository.findById(claimId)
                .map(claim -> claim.getUserPolicy().getUser().getId().equals(userId))
                .orElse(false);
    }
    
    // Get all claims (admin only)
    @Transactional(readOnly = true)
    public List<ClaimDto> getAllClaims() {
        List<Claim> allClaims = claimRepository.findAll();
        return claimMapper.toDtoList(allClaims);
    }

    // Check if user is the owner of the policy
    @Transactional(readOnly = true)
    public boolean isPolicyOwner(Long policyId, Long userId) {
        return userPolicyRepository.findById(policyId)
                .map(userPolicy -> userPolicy.getUser().getId().equals(userId))
                .orElse(false);
    }
}
