package com.example.insurance_app.payments;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {
    
    @Mapping(target = "userPolicyId", source = "userPolicy.id")
    @Mapping(target = "policyNumber", source = "userPolicy.policy.policyNumber")
    @Mapping(target = "userName", source = "userPolicy.user.name")
    @Mapping(target = "overdue", expression = "java(payment.isOverdue())")
    @Mapping(target = "daysOverdue", expression = "java(payment.getDaysOverdue())")
    @Mapping(target = "totalAmount", expression = "java(payment.getTotalAmount())")
    PaymentDto toDto(Payment payment);
    
    List<PaymentDto> toDtoList(List<Payment> payments);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paymentDate", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "userPolicy", ignore = true)
    @Mapping(target = "lateFeeAmount", constant = "0")
    @Mapping(target = "status", constant = "PENDING")
    Payment toEntity(PaymentCreateRequest request);
}
