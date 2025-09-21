package com.example.insurance_app.policies;

import com.example.insurance_app.exceptions.PolicyAlreadyExistsException;
import com.example.insurance_app.exceptions.PolicyNotFoundException;
import com.example.insurance_app.exceptions.UnauthorizedAccessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PolicyService {
    
    private final PolicyRepository policyRepository;
    private final PolicyMapper policyMapper;
    
    // Create a new policy (Admin only)
    public PolicyDto createPolicy(PolicyCreateRequest request) {
        validateAdminAccess();        
        Policy policy = policyMapper.toEntity(request);
        String policyNumber = generateUniquePolicyNumber(request.getType());
        policy.setPolicyNumber(policyNumber);
        Policy savedPolicy = policyRepository.save(policy);
        return policyMapper.toDto(savedPolicy);
    }
    
    // Update an existing policy (Admin only)
    public PolicyDto updatePolicy(Long id, PolicyUpdateRequest request) {
        validateAdminAccess();
        Policy existingPolicy = policyRepository.findById(id)
                .orElseThrow(() -> PolicyNotFoundException.byId(id));
        
        // Check if policy number is being updated and if it already exists
        if (request.getPolicyNumber() != null && 
            !request.getPolicyNumber().equals(existingPolicy.getPolicyNumber()) &&
            policyRepository.existsByPolicyNumber(request.getPolicyNumber())) {
            throw PolicyAlreadyExistsException.withPolicyNumber(request.getPolicyNumber());
        }
        policyMapper.updateEntity(request, existingPolicy);
        Policy updatedPolicy = policyRepository.save(existingPolicy);
        return policyMapper.toDto(updatedPolicy);
    }
    
    // Delete a policy (Admin only)
    public void deletePolicy(Long id) {
        validateAdminAccess();
        if (!policyRepository.existsById(id)) {
            throw PolicyNotFoundException.byId(id);
        }
        policyRepository.deleteById(id);
    }
    
    // Get policy by ID (Admin and Customer)
    @Transactional(readOnly = true)
    public PolicyDto getPolicyById(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> PolicyNotFoundException.byId(id));        
        return policyMapper.toDto(policy);
    }
    
    // Get policy by policy number (Admin and Customer)
    @Transactional(readOnly = true)
    public PolicyDto getPolicyByPolicyNumber(String policyNumber) {        
        Policy policy = policyRepository.findByPolicyNumber(policyNumber)
                .orElseThrow(() -> PolicyNotFoundException.byPolicyNumber(policyNumber));
        return policyMapper.toDto(policy);
    }
    
    // Get all policies (Admin and Customer)
    @Transactional(readOnly = true)
    public List<PolicyDto> getAllPolicies() {
        List<Policy> policies = policyRepository.findAllOrderByCreatedAtDesc();
        return policies.stream()
                .map(policyMapper::toDto)
                .collect(Collectors.toList());
    }
    
    // Get policies by type (Admin and Customer)
    @Transactional(readOnly = true)
    public List<PolicyDto> getPoliciesByType(Policy.PolicyType type) {
        List<Policy> policies = policyRepository.findByTypeOrderByPremiumAmountAsc(type);
        return policies.stream()
                .map(policyMapper::toDto)
                .collect(Collectors.toList());
    }
    
    // Search policies by description keyword (Admin and Customer)
    @Transactional(readOnly = true)
    public List<PolicyDto> searchPoliciesByKeyword(String keyword) {
        List<Policy> policies = policyRepository.findByDescriptionContainingIgnoreCase(keyword);
        return policies.stream()
                .map(policyMapper::toDto)
                .collect(Collectors.toList());
    }
    
    // Get policies by premium range (Admin and Customer)
    @Transactional(readOnly = true)
    public List<PolicyDto> getPoliciesByPremiumRange(BigDecimal minPremium, BigDecimal maxPremium) {
        List<Policy> policies = policyRepository.findByPremiumAmountBetween(minPremium, maxPremium);
        return policies.stream()
                .map(policyMapper::toDto)
                .collect(Collectors.toList());
    }
    
    // Get policies by minimum coverage amount (Admin and Customer)
    @Transactional(readOnly = true)
    public List<PolicyDto> getPoliciesByMinCoverage(BigDecimal minCoverage) {
        List<Policy> policies = policyRepository.findByCoverageAmountGreaterThan(minCoverage);
        return policies.stream()
                .map(policyMapper::toDto)
                .collect(Collectors.toList());
    }
    
    // Get policies by term years range (Admin and Customer)
    @Transactional(readOnly = true)
    public List<PolicyDto> getPoliciesByTermRange(Integer minYears, Integer maxYears) {
        List<Policy> policies = policyRepository.findByTermYearsBetween(minYears, maxYears);
        return policies.stream()
                .map(policyMapper::toDto)
                .collect(Collectors.toList());
    }
    
    // Generate a unique policy number based on policy type
    private String generateUniquePolicyNumber(Policy.PolicyType type) {
        String typeCode = getTypeCode(type);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        String policyNumber;
        do {
            String uniqueId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            policyNumber = String.format("%s-%s-%s", typeCode, timestamp, uniqueId);
        } while (policyRepository.existsByPolicyNumber(policyNumber));
        
        return policyNumber;
    }
    private String getTypeCode(Policy.PolicyType type) {
        return switch (type) {
            case HEALTH -> "HLT";
            case LIFE -> "LIF";
            case VEHICLE -> "VEH";
            case TRAVEL -> "TRV";
        };
    }
    
    // Validate if current user has admin access
    private void validateAdminAccess() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw UnauthorizedAccessException.forAction("policy management");
        }
        // Check if user has ADMIN role
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin) {
            throw UnauthorizedAccessException.forRole("ADMIN");
        }
    }
}
