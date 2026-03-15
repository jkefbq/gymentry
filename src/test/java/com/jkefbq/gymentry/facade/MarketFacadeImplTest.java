package com.jkefbq.gymentry.facade;

import com.jkefbq.gymentry.database.dto.TariffType;
import com.jkefbq.gymentry.dto.PurchaseDto;
import com.jkefbq.gymentry.dto.SubscriptionRequestDto;
import com.jkefbq.gymentry.service.MessageProducer;
import com.jkefbq.gymentry.service.SubscriptionPriceCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MarketFacadeImplTest {

    @Mock
    SubscriptionPriceCalculator subscriptionPriceCalculator;
    @Mock
    MessageProducer messageProducer;

    @InjectMocks
    MarketFacadeImpl marketFacade;

    @Test
    public void createTest() {
        ArgumentCaptor<PurchaseDto> captor = ArgumentCaptor.forClass(PurchaseDto.class);
        SubscriptionRequestDto request = new SubscriptionRequestDto(12, TariffType.BASIC);
        var email = "email";

        marketFacade.create(request, email);

        verify(messageProducer).sendSubscriptionPurchase(captor.capture());
        assertEquals(request.getVisitsTotal(), captor.getValue().getVisitsTotal());
        assertEquals(request.getTariffType(), captor.getValue().getTariffType());
        assertEquals(email, captor.getValue().getOwnerEmail().getEmail());
    }

    @Test
    public void calculatePriceTest() {
        var visitCount = 12;

        marketFacade.calculatePrice(TariffType.BASIC, visitCount);

        verify(subscriptionPriceCalculator).calculate(TariffType.BASIC, visitCount);
    }
}
