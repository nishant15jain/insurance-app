package com.example.insurance_app.claims;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClaimMapper {
    
    ClaimDto toDto(Claim claim);
    
    List<ClaimDto> toDtoList(List<Claim> claims);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userPolicy", ignore = true)
    @Mapping(target = "claimDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "processedBy", ignore = true)
    Claim toEntity(ClaimCreateRequest request);
}
