package com.example.insurance_app.user_policies;

import com.example.insurance_app.exceptions.PolicyOperationException;
import com.example.insurance_app.exceptions.PolicyPurchaseException;
import com.example.insurance_app.exceptions.UserPolicyNotFoundException;
import com.example.insurance_app.policies.Policy;
import com.example.insurance_app.policies.PolicyRepository;
import com.example.insurance_app.users.User;
import com.example.insurance_app.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPolicyService {
    
    private final UserPolicyRepository userPolicyRepository;
    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final UserPolicyMapper userPolicyMapper;
    
    // Purchase a policy for a user
    @Transactional
    public UserPolicyDto purchasePolicy(UserPolicyCreateRequest request) {
        log.info("Processing policy purchase request for user {} and policy {}", 
                request.getUserId(), request.getPolicyId());
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new PolicyPurchaseException("User not found with ID: " + request.getUserId()));
        Policy policy = policyRepository.findById(request.getPolicyId())
                .orElseThrow(() -> new PolicyPurchaseException("Policy not found with ID: " + request.getPolicyId()));
        if (userPolicyRepository.existsActiveUserPolicy(request.getUserId(), request.getPolicyId())) {
            throw PolicyPurchaseException.userAlreadyHasPolicy(request.getUserId(), request.getPolicyId());
        }
        validatePolicyStartDate(request.getStartDate());
        UserPolicy userPolicy = userPolicyMapper.toEntity(request);
        userPolicy.setUser(user);
        userPolicy.setPolicy(policy);
        
        // Calculate end date based on policy term years
        LocalDate endDate = request.getStartDate().plusYears(policy.getTermYears());
        userPolicy.setEndDate(endDate);
        
        // Calculate next premium due date based on policy frequency
        userPolicy.setNextPremiumDue(calculateNextPremiumDue(request.getStartDate(), policy));
        
        UserPolicy savedUserPolicy = userPolicyRepository.save(userPolicy);
        log.info("Successfully purchased policy {} for user {}", policy.getPolicyNumber(), user.getEmail());
        return userPolicyMapper.toDto(savedUserPolicy);
    }
    
    // Get all policies for a specific user
    @Transactional(readOnly = true)
    public List<UserPolicyDto> getUserPolicies(Long userId) {        
        List<UserPolicy> userPolicies = userPolicyRepository.findByUserIdWithDetails(userId);
        return userPolicyMapper.toDtoList(userPolicies);
    }
    
    // Get active policies for a specific user
    @Transactional(readOnly = true)
    public List<UserPolicyDto> getActivePoliciesForUser(Long userId) {
        List<UserPolicy> activePolicies = userPolicyRepository.findActiveByUserId(userId);
        return userPolicyMapper.toDtoList(activePolicies);
    }
    
    // Get user policy by ID
    @Transactional(readOnly = true)
    public UserPolicyDto getUserPolicyById(Long id) {
        UserPolicy userPolicy = userPolicyRepository.findById(id)
                .orElseThrow(() -> new UserPolicyNotFoundException(id));
        
        return userPolicyMapper.toDto(userPolicy);
    }
    
    // Update user policy
    @Transactional
    public UserPolicyDto updateUserPolicy(Long id, UserPolicyUpdateRequest request) {
        UserPolicy userPolicy = userPolicyRepository.findById(id)
                .orElseThrow(() -> new UserPolicyNotFoundException(id));
        
        // Note: End date is no longer updatable - it's fixed based on policy term
        if (request.getStatus() != null) {
            validateStatusTransition(userPolicy.getStatus(), request.getStatus());
        }
        userPolicyMapper.updateEntityFromDto(request, userPolicy);
        UserPolicy updatedUserPolicy = userPolicyRepository.save(userPolicy);
        return userPolicyMapper.toDto(updatedUserPolicy);
    }
    
    // Cancel a user policy
    @Transactional
    public UserPolicyDto cancelUserPolicy(Long userPolicyId) {
        log.info("Cancelling user policy with ID {}", userPolicyId);
        
        UserPolicy userPolicy = userPolicyRepository.findById(userPolicyId)
                .orElseThrow(() -> new UserPolicyNotFoundException(userPolicyId));
        
        if (userPolicy.getStatus() == UserPolicy.Status.CANCELLED) {
            throw PolicyOperationException.cannotCancel("Policy is already cancelled");
        }
        if (userPolicy.getStatus() == UserPolicy.Status.LAPSED) {
            throw PolicyOperationException.cannotCancel("Cannot cancel a lapsed policy");
        }
        userPolicy.setStatus(UserPolicy.Status.CANCELLED);
        UserPolicy updatedUserPolicy = userPolicyRepository.save(userPolicy);
        return userPolicyMapper.toDto(updatedUserPolicy);
    }
    
    // Renew a user policy
    @Transactional
    public UserPolicyDto renewUserPolicy(Long userPolicyId) {
        UserPolicy userPolicy = userPolicyRepository.findById(userPolicyId)
                .orElseThrow(() -> new UserPolicyNotFoundException(userPolicyId));
        
        if (userPolicy.getStatus() == UserPolicy.Status.CANCELLED) {
            throw PolicyOperationException.cannotRenew("Cannot renew a cancelled policy");
        }
        // Extend policy by its original term
        Policy policy = userPolicy.getPolicy();
        LocalDate newEndDate = userPolicy.getEndDate().plusYears(policy.getTermYears());
        LocalDate newPremiumDue = calculateNextPremiumDue(LocalDate.now(), policy);
        
        userPolicy.setEndDate(newEndDate);
        userPolicy.setNextPremiumDue(newPremiumDue);
        userPolicy.setStatus(UserPolicy.Status.ACTIVE);
        UserPolicy renewedUserPolicy = userPolicyRepository.save(userPolicy);
        return userPolicyMapper.toDto(renewedUserPolicy);
    }
    
    // Get policies expiring within specified days
    @Transactional(readOnly = true)
    public List<UserPolicyDto> getPoliciesExpiringIn(int days) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(days);
        
        List<UserPolicy> expiringPolicies = userPolicyRepository.findPoliciesExpiringBetween(startDate, endDate);
        return userPolicyMapper.toDtoList(expiringPolicies);
    }
    
    // Get policies due for renewal
    @Transactional(readOnly = true)
    public List<UserPolicyDto> getPoliciesDueForRenewal(int daysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(daysAhead);
        
        List<UserPolicy> policiesDueForRenewal = userPolicyRepository.findPoliciesDueForRenewal(today, dueDate);
        return userPolicyMapper.toDtoList(policiesDueForRenewal);
    }
    
    // Get user policies by policy type
    @Transactional(readOnly = true)
    public List<UserPolicyDto> getUserPoliciesByType(Long userId, Policy.PolicyType policyType) {
        List<UserPolicy> userPolicies = userPolicyRepository.findByUserIdAndPolicyType(userId, policyType);
        return userPolicyMapper.toDtoList(userPolicies);
    }
    
    // Count active policies for a user
    @Transactional(readOnly = true)
    public long countActivePoliciesForUser(Long userId) {
        return userPolicyRepository.countActiveByUserId(userId);
    }
    
    // Get all user policies (Admin only)
    @Transactional(readOnly = true)
    public List<UserPolicyDto> getAllUserPolicies() {
        List<UserPolicy> allUserPolicies = userPolicyRepository.findAll();
        return userPolicyMapper.toDtoList(allUserPolicies);
    }
    
    // Private helper methods
    
    private void validatePolicyStartDate(LocalDate startDate) {
        if (startDate == null) {
            throw PolicyPurchaseException.invalidDates("Start date is required");
        }
        
        if (startDate.isBefore(LocalDate.now().minusDays(1))) {
            throw PolicyPurchaseException.invalidDates("Start date cannot be in the past");
        }
    }
    
    private void validateStatusTransition(UserPolicy.Status currentStatus, UserPolicy.Status newStatus) {
        // Allow transitioning from CANCELLED to ACTIVE (policy reactivation)
        if (currentStatus == UserPolicy.Status.CANCELLED && newStatus == UserPolicy.Status.ACTIVE) {
            return; // Allow this transition
        }
        
        // Prevent other transitions from CANCELLED status (except to ACTIVE)
        if (currentStatus == UserPolicy.Status.CANCELLED && newStatus != UserPolicy.Status.CANCELLED) {
            throw PolicyOperationException.invalidStatusTransition(currentStatus.name(), newStatus.name());
        }
        
        // Prevent transitions from LAPSED to ACTIVE (should use renewal instead)
        if (currentStatus == UserPolicy.Status.LAPSED && newStatus == UserPolicy.Status.ACTIVE) {
            throw PolicyOperationException.invalidStatusTransition(currentStatus.name(), newStatus.name());
        }
        
        // Add more validation rules as needed
    }
    
    private LocalDate calculateNextPremiumDue(LocalDate startDate, Policy policy) {
        // Calculate next premium due based on premium frequency
        int monthsBetweenPayments = policy.getPremiumFrequency().getMonthsBetweenPayments();
        return startDate.plusMonths(monthsBetweenPayments);
    }
}
