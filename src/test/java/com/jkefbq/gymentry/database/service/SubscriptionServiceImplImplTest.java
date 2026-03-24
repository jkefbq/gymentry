package com.jkefbq.gymentry.database.service;

import com.jkefbq.gymentry.dto.for_entity.SubscriptionDto;
import com.jkefbq.gymentry.dto.for_entity.TariffType;
import com.jkefbq.gymentry.database.entity.Subscription;
import com.jkefbq.gymentry.database.mapper.SubscriptionMapper;
import com.jkefbq.gymentry.database.mapper.SubscriptionMapperImpl;
import com.jkefbq.gymentry.database.repository.SubscriptionRepository;
import com.jkefbq.gymentry.exception.NonActiveSubscriptionException;
import com.jkefbq.gymentry.service.database.SubscriptionServiceImpl;
import com.jkefbq.gymentry.service.database.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceImplImplTest {

    private static final String EMAIL = "email@gmail.com";
    private static final BigDecimal DEF_SUB_SNAPSHOT_PRICE = BigDecimal.TEN;

    @Mock
    SubscriptionRepository subscriptionRepository;
    @Spy
    SubscriptionMapper subscriptionMapper = new SubscriptionMapperImpl();
    @Mock
    UserService userService;

    @Spy
    @InjectMocks
    SubscriptionServiceImpl subscriptionServiceImpl;

    public SubscriptionDto getSubDto(boolean isActive) {
        return SubscriptionDto.builder()
                .id(UUID.randomUUID())
                .active(isActive)
                .snapshotPrice(DEF_SUB_SNAPSHOT_PRICE)
                .tariffType(TariffType.BASIC)
                .visitsLeft(5)
                .visitsTotal(10)
                .purchaseAt(LocalDate.now())
                .build();
    }

    @Test
    public void createTest_notLastVisit_assertActive() {
        Subscription entity = subscriptionMapper.toEntity(getSubDto(true));
        when(subscriptionRepository.save(any())).thenReturn(entity);
        var sub = subscriptionServiceImpl.sendCreateMessage(getSubDto(true));

        verify(subscriptionRepository).save(any());
        assertTrue(sub.getActive());
    }

    @Test
    public void createTest_lastVisit_assertNotActive() {
        SubscriptionDto sub = getSubDto(true);
        sub.setVisitsLeft(0);
        doAnswer(invocation ->
                invocation.getArgument(0)
        ).when(subscriptionRepository).save(any());


        var savedSub = subscriptionServiceImpl.sendCreateMessage(sub);

        verify(subscriptionRepository).save(any());
        assertFalse(savedSub.getActive());
    }

    @Test
    public void updateTest() {
        Subscription entity = subscriptionMapper.toEntity(getSubDto(true));
        when(subscriptionRepository.save(any())).thenReturn(entity);

        var sub = subscriptionServiceImpl.sendCreateMessage(getSubDto(true));

        verify(subscriptionRepository).save(any());
        assertTrue(sub.getActive());
    }

    @Test
    public void validateAndGetActiveSubscription_throwNonActiveSubscriptionException() {
        doAnswer(invocation ->
            List.of(getSubDto(false), getSubDto(false), getSubDto(false))
        ).when(subscriptionServiceImpl).getUserSubs(any());
        var userId = UUID.randomUUID();

        assertThrows(NonActiveSubscriptionException.class, () -> subscriptionServiceImpl.getActiveSubscription(userId));
    }

    @Test
    public void validateAndGetActiveSubscription_throwIllegalStateException() {
        doAnswer(invocation ->
                List.of(getSubDto(true), getSubDto(true), getSubDto(false))
        ).when(subscriptionServiceImpl).getUserSubs(any());
        var userId = UUID.randomUUID();

        assertThrows(IllegalStateException.class, () -> subscriptionServiceImpl.getActiveSubscription(userId));
    }

    @Test
    public void activateSubscriptionTest() {
        doAnswer(invocation -> Optional.of(getSubDto(false)))
                .when(subscriptionServiceImpl).findById(any());
        doAnswer(invocation -> invocation.getArgument(0))
                .when(subscriptionServiceImpl).update(any());

        SubscriptionDto sub = subscriptionServiceImpl.activateSubscription(UUID.randomUUID());

        assertTrue(sub.getActive());
    }

    @Test
    public void getAllForPeriodTest() {
        var from = LocalDate.now();
        var to = LocalDate.now();

        subscriptionServiceImpl.getAllForPeriod(from, to);

        verify(subscriptionRepository).getAllForPeriod(from, to);
    }

    @Test
    public void findByIdTest() {
        subscriptionServiceImpl.findById(UUID.randomUUID());
        verify(subscriptionRepository).findById(any());
    }

    @Test
    public void deactivateSubscriptionTest() {
        var sub = getSubDto(true);
        when(subscriptionServiceImpl.findById(sub.getId())).thenReturn(Optional.of(sub));
        doAnswer(invocation -> invocation.getArgument(0)).when(subscriptionServiceImpl).update(any());
        var updSub = subscriptionServiceImpl.deactivateSubscription(sub.getId());

        assertFalse(updSub.getActive());
    }

}
