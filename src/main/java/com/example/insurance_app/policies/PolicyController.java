package com.example.insurance_app.policies;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
@Tag(name = "Policy Management", description = "APIs for managing insurance policies")
public class PolicyController {
    
    private final PolicyService policyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PolicyDto> createPolicy(
            @Valid @RequestBody PolicyCreateRequest request) {
        PolicyDto createdPolicy = policyService.createPolicy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPolicy);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PolicyDto> updatePolicy(
            @Parameter(description = "Policy ID") @PathVariable Long id,
            @Valid @RequestBody PolicyUpdateRequest request) {
        PolicyDto updatedPolicy = policyService.updatePolicy(id, request);
        return ResponseEntity.ok(updatedPolicy);
    }
    
    // Delete a policy (Admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePolicy(
            @Parameter(description = "Policy ID") @PathVariable Long id) {
        policyService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }
    
    // Get policy by ID (Admin and Customer)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<PolicyDto> getPolicyById(
            @Parameter(description = "Policy ID") @PathVariable Long id) {
        PolicyDto policy = policyService.getPolicyById(id);
        return ResponseEntity.ok(policy);
    }
    
    // Get policy by policy number (Admin and Customer)
    @GetMapping("/policy-number/{policyNumber}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<PolicyDto> getPolicyByPolicyNumber(
            @Parameter(description = "Policy Number") @PathVariable String policyNumber) {
        PolicyDto policy = policyService.getPolicyByPolicyNumber(policyNumber);
        return ResponseEntity.ok(policy);
    }
    
    // Get all policies (Admin and Customer)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<List<PolicyDto>> getAllPolicies() {
        List<PolicyDto> policies = policyService.getAllPolicies();
        return ResponseEntity.ok(policies);
    }
    
    // Get policies by type (Admin and Customer)
    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<List<PolicyDto>> getPoliciesByType(
            @Parameter(description = "Policy Type") @PathVariable Policy.PolicyType type) {
        List<PolicyDto> policies = policyService.getPoliciesByType(type);
        return ResponseEntity.ok(policies);
    }
    
    // Search policies by keyword (Admin and Customer)
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<List<PolicyDto>> searchPolicies(
            @Parameter(description = "Search keyword") @RequestParam String keyword) {
        List<PolicyDto> policies = policyService.searchPoliciesByKeyword(keyword);
        return ResponseEntity.ok(policies);
    }
    
    // Get policies by premium range (Admin and Customer)
    @GetMapping("/premium-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<List<PolicyDto>> getPoliciesByPremiumRange(
            @Parameter(description = "Minimum premium amount") @RequestParam BigDecimal minPremium,
            @Parameter(description = "Maximum premium amount") @RequestParam BigDecimal maxPremium) {
        List<PolicyDto> policies = policyService.getPoliciesByPremiumRange(minPremium, maxPremium);
        return ResponseEntity.ok(policies);
    }
    
    // Get policies by minimum coverage (Admin and Customer)
    @GetMapping("/min-coverage")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<List<PolicyDto>> getPoliciesByMinCoverage(
            @Parameter(description = "Minimum coverage amount") @RequestParam BigDecimal minCoverage) {
        List<PolicyDto> policies = policyService.getPoliciesByMinCoverage(minCoverage);
        return ResponseEntity.ok(policies);
    }
    
    // Get policies by term years range (Admin and Customer)
    @GetMapping("/term-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<List<PolicyDto>> getPoliciesByTermRange(
            @Parameter(description = "Minimum term years") @RequestParam Integer minYears,
            @Parameter(description = "Maximum term years") @RequestParam Integer maxYears) {
        List<PolicyDto> policies = policyService.getPoliciesByTermRange(minYears, maxYears);
        return ResponseEntity.ok(policies);
    }
}
