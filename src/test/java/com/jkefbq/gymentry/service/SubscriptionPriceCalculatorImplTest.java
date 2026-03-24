package com.jkefbq.gymentry.service;

import com.jkefbq.gymentry.dto.for_entity.SubscriptionDto;
import com.jkefbq.gymentry.dto.for_entity.TariffDto;
import com.jkefbq.gymentry.dto.for_entity.TariffType;
import com.jkefbq.gymentry.service.database.TariffService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class SubscriptionPriceCalculatorImplTest {

    @Mock
    TariffService tariffService;

    @InjectMocks
    SubscriptionPriceCalculatorImpl subPriceCalc;

    @Test
    public void calculateTest_subscriptionDto() {
        SubscriptionDto sub = SubscriptionDto.builder().visitsTotal(23).build();
        var pricePerLesson = BigDecimal.ONE;
        when(tariffService.getByType(any())).thenReturn(Optional.of(TariffDto.builder().pricePerLesson(pricePerLesson).build()));

        BigDecimal price = subPriceCalc.calculate(sub);

        var trueResult = pricePerLesson.multiply(BigDecimal.valueOf(sub.getVisitsTotal()));
        assertEquals(trueResult, price);
    }

    @Test
    public void calculateTest() {
        var tariffType = TariffType.PREMIUM;
        var visitsCount = 7;
        var pricePerLesson = BigDecimal.TEN;
        when(tariffService.getByType(any())).thenReturn(Optional.of(TariffDto.builder().pricePerLesson(pricePerLesson).build()));

        BigDecimal price = subPriceCalc.calculate(tariffType, visitsCount);

        var trueResult = pricePerLesson.multiply(BigDecimal.valueOf(visitsCount));
        assertEquals(trueResult, price);
    }

}
