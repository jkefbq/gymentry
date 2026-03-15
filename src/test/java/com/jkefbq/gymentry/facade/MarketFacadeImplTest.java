package com.jkefbq.gymentry.facade;

import com.jkefbq.gymentry.database.dto.PartialUserDto;
import com.jkefbq.gymentry.database.dto.TariffType;
import com.jkefbq.gymentry.database.mapper.SubscriptionMapper;
import com.jkefbq.gymentry.database.mapper.SubscriptionMapperImpl;
import com.jkefbq.gymentry.database.service.SubscriptionAnalytics;
import com.jkefbq.gymentry.database.service.SubscriptionManager;
import com.jkefbq.gymentry.database.service.UserService;
import com.jkefbq.gymentry.dto.SubscriptionRequestDto;
import com.jkefbq.gymentry.service.SubscriptionPriceCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MarketFacadeImplTest {

    @Mock
    SubscriptionPriceCalculator subscriptionPriceCalculator;
    @Mock
    UserService userService;
    @Mock
    SubscriptionAnalytics subscriptionAnalytics;
    @Mock
    SubscriptionManager subscriptionManager;
    @Spy
    SubscriptionMapper subscriptionMapper = new SubscriptionMapperImpl();

    @InjectMocks
    MarketFacadeImpl marketFacade;

    @Test
    public void createTest() {
        when(userService.findByEmail(any())).thenReturn(Optional.of(PartialUserDto.builder().id(UUID.randomUUID()).build()));
        SubscriptionRequestDto request = new SubscriptionRequestDto(12, TariffType.BASIC);
        var email = "email";

        marketFacade.create(request, email);

        verify(subscriptionManager).create(any());
        verify(subscriptionMapper).toResponseDto(any());
    }

    @Test
    public void calculatePriceTest() {
        var visitCount = 12;

        marketFacade.calculatePrice(TariffType.BASIC, visitCount);

        verify(subscriptionPriceCalculator).calculate(TariffType.BASIC, visitCount);
    }
}
