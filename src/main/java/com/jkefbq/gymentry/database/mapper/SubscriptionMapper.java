package com.jkefbq.gymentry.database.mapper;

import com.jkefbq.gymentry.dto.for_entity.SubscriptionDto;
import com.jkefbq.gymentry.dto.for_entity.SubscriptionRequestDto;
import com.jkefbq.gymentry.dto.for_entity.SubscriptionResponseDto;
import com.jkefbq.gymentry.database.entity.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    @Mapping(target = "userId", source = "user.id")
    SubscriptionDto toDto(Subscription entity);
    @Mapping(target = "user.id", source = "userId")
    Subscription toEntity(SubscriptionDto dto);

    SubscriptionRequestDto toRequestDto(SubscriptionDto subscriptionDto);
    SubscriptionResponseDto toResponseDto(SubscriptionDto subscriptionDto);
    SubscriptionDto toDto(SubscriptionRequestDto requestDto);
}