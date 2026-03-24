package com.jkefbq.gymentry.facade;

import com.jkefbq.gymentry.dto.for_entity.SubscriptionDto;
import com.jkefbq.gymentry.dto.for_entity.TariffType;
import com.jkefbq.gymentry.dto.for_entity.VisitDto;
import com.jkefbq.gymentry.service.database.SubscriptionAnalyticsService;
import com.jkefbq.gymentry.service.database.SubscriptionService;
import com.jkefbq.gymentry.service.database.VisitAnalyticsService;
import com.jkefbq.gymentry.service.database.VisitService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminStatisticsFacadeTest {

    private static final String MOCK_ADDRESS = "address";

    @Mock
    VisitService visitService;
    @Mock
    VisitAnalyticsService visitAnalyticsService;
    @Mock
    SubscriptionAnalyticsService subscriptionAnalyticsService;
    @Mock
    SubscriptionService subscriptionService;

    @InjectMocks
    AdminStatisticsFacade adminStat;

    public List<VisitDto> getVisits() {
        var visit = VisitDto.builder().createdAt(LocalDateTime.now()).id(UUID.randomUUID()).build();
        return List.of(visit, visit, visit, visit, visit, visit);
    }

    public List<SubscriptionDto> getSubscriptions() {
        var sub = SubscriptionDto.builder().active(false).visitsLeft(4).purchaseAt(LocalDate.now())
                .tariffType(TariffType.BASIC).snapshotPrice(BigDecimal.TEN).visitsTotal(10).build();
        return List.of(sub, sub, sub, sub, sub, sub, sub, sub);
    }

    @Test
    public void getVisitStatisticsForPeriodTest() {
        var from = LocalDateTime.now();
        var to = LocalDateTime.now();
        var wholeDays = ChronoUnit.DAYS.between(from, to);
        var visits = getVisits();
        when(visitService.getAllForPeriod(from, to, MOCK_ADDRESS)).thenReturn(visits);

        adminStat.getVisitStatisticsForPeriod(from, to, MOCK_ADDRESS);

        verify(visitAnalyticsService).getAvgPerDay(visits.size(), wholeDays);
        verify(visitAnalyticsService).getPeakDay(visits);
        verify(visitAnalyticsService).getVisitsPerDate(visits);
        verify(visitAnalyticsService).getTariffsPerDate(visits);
    }

    @Test
    public void getPurchaseStatisticsForPeriodTest() {
        var from = LocalDate.now();
        var to = LocalDate.now();
        var wholeDays = ChronoUnit.DAYS.between(from, to);
        var subscriptions = getSubscriptions();
        when(subscriptionService.getAllForPeriod(from, to)).thenReturn(subscriptions);

        adminStat.getPurchaseStatisticsForPeriod(from, to);

        verify(subscriptionAnalyticsService).getTotalRevenue(subscriptions);
        verify(subscriptionAnalyticsService).getAvgDayCheck(subscriptions, wholeDays);
        verify(subscriptionAnalyticsService).getAvgPerPurchaseCheck(subscriptions);
        verify(subscriptionAnalyticsService).getPeakDay(subscriptions);
        verify(subscriptionAnalyticsService).getPurchasesPerDate(subscriptions);
        verify(subscriptionAnalyticsService).getPurchasesPerTariff(subscriptions);
    }
}
