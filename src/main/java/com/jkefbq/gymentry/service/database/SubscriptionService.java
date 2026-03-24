package com.jkefbq.gymentry.service.database;

import com.jkefbq.gymentry.dto.for_entity.SubscriptionDto;
import com.jkefbq.gymentry.dto.for_entity.SubscriptionRequestDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionService {
    Optional<SubscriptionDto> findById(UUID id);
    SubscriptionDto sendCreateMessage(SubscriptionDto dto);
    void sendCreateMessage(SubscriptionRequestDto requestDto, String email);
    SubscriptionDto update(SubscriptionDto dto);
    SubscriptionDto activateSubscription(UUID subscriptionId);
    List<SubscriptionDto> getAllForPeriod(LocalDate from, LocalDate to);
    SubscriptionDto deactivateSubscription(UUID subscriptionId);
    List<SubscriptionDto> getUserSubs(UUID userId);
    SubscriptionDto getActiveSubscription(UUID userId);
    void checkActiveSub(SubscriptionDto sub);
}
