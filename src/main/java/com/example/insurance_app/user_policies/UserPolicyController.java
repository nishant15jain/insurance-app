package com.example.insurance_app.user_policies;

import com.example.insurance_app.policies.Policy;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-policies")
@RequiredArgsConstructor
@Tag(name = "User Policy Management", description = "APIs for managing user policy relationships")
public class UserPolicyController {
    
    private final UserPolicyService userPolicyService;
    
    @PostMapping("/purchase")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    @Operation(summary = "Purchase a policy", description = "Customer purchases an insurance policy")
    public ResponseEntity<UserPolicyDto> purchasePolicy(
            @Valid @RequestBody UserPolicyCreateRequest request) {
        UserPolicyDto userPolicy = userPolicyService.purchasePolicy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userPolicy);
    }
    
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and #userId == authentication.principal.id)")
    @Operation(summary = "Get all policies for a user", description = "Retrieve all policies associated with a specific user")
    public ResponseEntity<List<UserPolicyDto>> getUserPolicies(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        List<UserPolicyDto> userPolicies = userPolicyService.getUserPolicies(userId);
        return ResponseEntity.ok(userPolicies);
    }
    
    @GetMapping("/user/{userId}/active")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and #userId == authentication.principal.id)")
    @Operation(summary = "Get active policies for a user", description = "Retrieve only active policies for a specific user")
    public ResponseEntity<List<UserPolicyDto>> getActivePoliciesForUser(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        List<UserPolicyDto> activePolicies = userPolicyService.getActivePoliciesForUser(userId);
        return ResponseEntity.ok(activePolicies);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    @Operation(summary = "Get user policy by ID", description = "Retrieve a specific user policy by its ID")
    public ResponseEntity<UserPolicyDto> getUserPolicyById(
            @Parameter(description = "User Policy ID") @PathVariable Long id) {
        UserPolicyDto userPolicy = userPolicyService.getUserPolicyById(id);
        return ResponseEntity.ok(userPolicy);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user policy", description = "Update user policy details (Admin only)")
    public ResponseEntity<UserPolicyDto> updateUserPolicy(
            @Parameter(description = "User Policy ID") @PathVariable Long id,
            @Valid @RequestBody UserPolicyUpdateRequest request) {
        UserPolicyDto updatedUserPolicy = userPolicyService.updateUserPolicy(id, request);
        return ResponseEntity.ok(updatedUserPolicy);
    }
    
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    @Operation(summary = "Cancel user policy", description = "Cancel a user's policy")
    public ResponseEntity<UserPolicyDto> cancelUserPolicy(
            @Parameter(description = "User Policy ID") @PathVariable Long id) {
        UserPolicyDto cancelledPolicy = userPolicyService.cancelUserPolicy(id);
        return ResponseEntity.ok(cancelledPolicy);
    }
    
    @PostMapping("/{id}/renew")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    @Operation(summary = "Renew user policy", description = "Renew a user's policy")
    public ResponseEntity<UserPolicyDto> renewUserPolicy(
            @Parameter(description = "User Policy ID") @PathVariable Long id) {
        UserPolicyDto renewedPolicy = userPolicyService.renewUserPolicy(id);
        return ResponseEntity.ok(renewedPolicy);
    }
    
    @GetMapping("/user/{userId}/type/{policyType}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and #userId == authentication.principal.id)")
    @Operation(summary = "Get user policies by type", description = "Retrieve user policies of a specific type")
    public ResponseEntity<List<UserPolicyDto>> getUserPoliciesByType(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Policy Type") @PathVariable Policy.PolicyType policyType) {
        List<UserPolicyDto> userPolicies = userPolicyService.getUserPoliciesByType(userId, policyType);
        return ResponseEntity.ok(userPolicies);
    }
    
    @GetMapping("/user/{userId}/count/active")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and #userId == authentication.principal.id)")
    @Operation(summary = "Count active policies", description = "Get count of active policies for a user")
    public ResponseEntity<Long> countActivePoliciesForUser(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        long count = userPolicyService.countActivePoliciesForUser(userId);
        return ResponseEntity.ok(count);
    }
    
    // Admin-only endpoints
    @GetMapping("/expiring")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get policies expiring soon", description = "Admin endpoint to get policies expiring within specified days")
    public ResponseEntity<List<UserPolicyDto>> getPoliciesExpiringIn(
            @Parameter(description = "Number of days") @RequestParam(defaultValue = "30") int days) {
        List<UserPolicyDto> expiringPolicies = userPolicyService.getPoliciesExpiringIn(days);
        return ResponseEntity.ok(expiringPolicies);
    }
    
    @GetMapping("/due-for-renewal")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get policies due for renewal", description = "Admin endpoint to get policies due for premium payment")
    public ResponseEntity<List<UserPolicyDto>> getPoliciesDueForRenewal(
            @Parameter(description = "Number of days ahead") @RequestParam(defaultValue = "7") int daysAhead) {
        List<UserPolicyDto> policiesDueForRenewal = userPolicyService.getPoliciesDueForRenewal(daysAhead);
        return ResponseEntity.ok(policiesDueForRenewal);
    }
    
    // Bulk operations for admin
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all user policies", description = "Admin endpoint to get all user policies in the system")
    public ResponseEntity<List<UserPolicyDto>> getAllUserPolicies() {
        List<UserPolicyDto> allUserPolicies = userPolicyService.getAllUserPolicies();
        return ResponseEntity.ok(allUserPolicies);
    }
    
}
