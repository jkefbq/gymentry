package com.jkefbq.gymentry.service;

import com.jkefbq.gymentry.dto.for_entity.SubscriptionDto;
import com.jkefbq.gymentry.dto.for_entity.TariffDto;
import com.jkefbq.gymentry.service.database.TariffService;
import com.jkefbq.gymentry.dto.for_entity.TariffType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class SubscriptionPriceCalculatorImpl implements SubscriptionPriceCalculator {

    private final TariffService tariffService;

    @Override
    @Transactional
    public BigDecimal calculate(SubscriptionDto subscriptionDto) {
        TariffDto tariffDto = tariffService.getByType(subscriptionDto.getTariffType())
                .orElseThrow(() -> new NoSuchElementException("tariff with type " + subscriptionDto.getTariffType() + " not found"));
        return tariffDto.getPricePerLesson().multiply(
                BigDecimal.valueOf(subscriptionDto.getVisitsTotal())
        );
    }

    @Override
    @Transactional
    public BigDecimal calculate(TariffType tariffType, Integer visitsCount) {
        TariffDto tariffDto = tariffService.getByType(tariffType)
                .orElseThrow(() -> new NoSuchElementException("tariff with type " + tariffType + " not found"));
        return tariffDto.getPricePerLesson().multiply(BigDecimal.valueOf(visitsCount));
    }
}
