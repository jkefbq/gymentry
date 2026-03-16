package com.jkefbq.gymentry.service;

import com.jkefbq.gymentry.database.dto.SubscriptionDto;
import com.jkefbq.gymentry.database.dto.TariffType;

import java.math.BigDecimal;

public interface SubscriptionPriceCalculator {
    BigDecimal calculate(SubscriptionDto subscriptionDto);
    BigDecimal calculate(TariffType tariffType, Integer visitsCount);
}
