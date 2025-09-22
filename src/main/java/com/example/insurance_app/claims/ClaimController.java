package com.example.insurance_app.claims;

import com.example.insurance_app.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
@Tag(name = "Claims Management", description = "APIs for managing insurance claims")
public class ClaimController {
    
    private final ClaimService claimService;
    
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER') or (hasRole('ADMIN') and @claimService.isPolicyOwner(#request.userPolicyId, authentication.principal.id))")
    @Operation(summary = "Submit a new claim", description = "Customer or admin-owner submits a new insurance claim")
    public ResponseEntity<ClaimDto> submitClaim(
            @Valid @RequestBody ClaimCreateRequest request) {
        ClaimDto claim = claimService.submitClaim(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(claim);
    }
        
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and #userId == authentication.principal.id)")
    @Operation(summary = "Get claims by user", description = "Retrieve all claims for a specific user")
    public ResponseEntity<List<ClaimDto>> getClaimsByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        List<ClaimDto> claims = claimService.getClaimsByUserId(userId);
        return ResponseEntity.ok(claims);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT') or @claimService.isClaimOwner(#id, authentication.principal.id)")
    @Operation(summary = "Get claim by ID", description = "Retrieve a specific claim by its ID")
    public ResponseEntity<ClaimDto> getClaimById(
            @Parameter(description = "Claim ID") @PathVariable Long id) {
        ClaimDto claim = claimService.getClaimById(id);
        return ResponseEntity.ok(claim);
    }
    
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    @Operation(summary = "Get pending claims", description = "Retrieve all pending claims for review")
    public ResponseEntity<List<ClaimDto>> getPendingClaims() {
        List<ClaimDto> pendingClaims = claimService.getPendingClaims();
        return ResponseEntity.ok(pendingClaims);
    }
    
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    @Operation(summary = "Approve a claim", description = "Approve a pending claim")
    public ResponseEntity<ClaimDto> approveClaim(
            @Parameter(description = "Claim ID") @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ClaimDto approvedClaim = claimService.approveClaim(id, userPrincipal.getId());
        return ResponseEntity.ok(approvedClaim);
    }
    
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    @Operation(summary = "Reject a claim", description = "Reject a pending claim")
    public ResponseEntity<ClaimDto> rejectClaim(
            @Parameter(description = "Claim ID") @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ClaimDto rejectedClaim = claimService.rejectClaim(id, userPrincipal.getId());
        return ResponseEntity.ok(rejectedClaim);
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all claims", description = "Admin endpoint to retrieve all claims in the system")
    public ResponseEntity<List<ClaimDto>> getAllClaims() {
        List<ClaimDto> allClaims = claimService.getAllClaims();
        return ResponseEntity.ok(allClaims);
    }
}
