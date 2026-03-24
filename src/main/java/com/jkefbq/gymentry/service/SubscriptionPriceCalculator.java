package com.jkefbq.gymentry.service;

import com.jkefbq.gymentry.dto.for_entity.SubscriptionDto;
import com.jkefbq.gymentry.dto.for_entity.TariffType;

import java.math.BigDecimal;

public interface SubscriptionPriceCalculator {
    BigDecimal calculate(SubscriptionDto subscriptionDto);
    BigDecimal calculate(TariffType tariffType, Integer visitsCount);
}
