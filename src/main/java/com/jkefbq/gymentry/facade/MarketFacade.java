package com.jkefbq.gymentry.facade;

import com.jkefbq.gymentry.database.dto.TariffType;
import com.jkefbq.gymentry.dto.SubscriptionRequestDto;

import java.math.BigDecimal;

public interface MarketFacade {
    void create(SubscriptionRequestDto requestDto, String email);
    BigDecimal calculatePrice(TariffType tariffType, Integer visitsCount);
}
