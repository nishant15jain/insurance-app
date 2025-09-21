package com.example.insurance_app.policies;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PolicyMapper {
    
    @Mapping(target = "typeDisplayName", expression = "java(policy.getType().getDisplayName())")
    @Mapping(target = "premiumFrequencyDisplayName", expression = "java(policy.getPremiumFrequency().getDisplayName())")
    PolicyDto toDto(Policy policy);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "policyNumber", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Policy toEntity(PolicyCreateRequest request);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(PolicyUpdateRequest request, @MappingTarget Policy policy);
}
