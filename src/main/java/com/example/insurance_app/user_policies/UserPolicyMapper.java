package com.example.insurance_app.user_policies;

import com.example.insurance_app.policies.Policy;
import com.example.insurance_app.policies.PolicyMapper;
import com.example.insurance_app.users.User;
import com.example.insurance_app.users.UserMapper;
import org.mapstruct.*;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, PolicyMapper.class})
public interface UserPolicyMapper {
    
    // Convert UserPolicy entity to UserPolicyDto
    @Mapping(target = "active", expression = "java(userPolicy.isActive())")
    @Mapping(target = "expired", expression = "java(userPolicy.isExpired())")
    @Mapping(target = "premiumDue", expression = "java(userPolicy.isPremiumDue())")
    @Mapping(target = "daysUntilExpiry", expression = "java(calculateDaysUntilExpiry(userPolicy.getEndDate()))")
    @Mapping(target = "daysUntilPremiumDue", expression = "java(calculateDaysUntilPremiumDue(userPolicy.getNextPremiumDue()))")
    UserPolicyDto toDto(UserPolicy userPolicy);

    // Convert list of UserPolicy entities to list of UserPolicyDto
    List<UserPolicyDto> toDtoList(List<UserPolicy> userPolicies);
    
    // Convert UserPolicyCreateRequest to UserPolicy entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "userId", qualifiedByName = "userIdToUser")
    @Mapping(target = "policy", source = "policyId", qualifiedByName = "policyIdToPolicy")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", ignore = true)
    UserPolicy toEntity(UserPolicyCreateRequest request);

    // Update UserPolicy entity from UserPolicyUpdateRequest
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "policy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UserPolicyUpdateRequest request, @MappingTarget UserPolicy userPolicy);
    
    // Helper method to calculate days until expiry
    default long calculateDaysUntilExpiry(LocalDate endDate) {
        if (endDate == null) return -1;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), endDate);
    }
    
    // Helper method to calculate days until premium due
    default long calculateDaysUntilPremiumDue(LocalDate nextPremiumDue) {
        if (nextPremiumDue == null) return -1;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), nextPremiumDue);
    }
    
    // Named mapping for userId to User entity
    @Named("userIdToUser")
    default User userIdToUser(Long userId) {
        if (userId == null) return null;
        User user = new User();
        user.setId(userId);
        return user;
    }
    
    // Named mapping for policyId to Policy entity
    @Named("policyIdToPolicy")
    default Policy policyIdToPolicy(Long policyId) {
        if (policyId == null) return null;
        Policy policy = new Policy();
        policy.setId(policyId);
        return policy;
    }
}
