package com.jkefbq.gymentry.facade;

import com.jkefbq.gymentry.database.dto.TariffType;
import com.jkefbq.gymentry.dto.EmailDto;
import com.jkefbq.gymentry.dto.PurchaseDto;
import com.jkefbq.gymentry.dto.SubscriptionRequestDto;
import com.jkefbq.gymentry.service.MessageProducer;
import com.jkefbq.gymentry.service.SubscriptionPriceCalculator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class MarketFacadeImpl implements MarketFacade {

    private final SubscriptionPriceCalculator subscriptionPriceCalculator;
    private final MessageProducer messageProducer;

    @Override
    @Transactional
    public void create(SubscriptionRequestDto requestDto, String email) {
        PurchaseDto purchase = new PurchaseDto(
                requestDto.getVisitsTotal(), requestDto.getTariffType(), new EmailDto(email)
        );
        messageProducer.sendSubscriptionPurchase(purchase);
    }

    @Override
    @Transactional
    public BigDecimal calculatePrice(TariffType tariffType, Integer visitsCount) {
        return subscriptionPriceCalculator.calculate(tariffType, visitsCount);
    }
}
