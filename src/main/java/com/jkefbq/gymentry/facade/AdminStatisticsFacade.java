package com.jkefbq.gymentry.facade;

import com.jkefbq.gymentry.dto.for_entity.SubscriptionDto;
import com.jkefbq.gymentry.dto.for_entity.VisitDto;
import com.jkefbq.gymentry.service.database.SubscriptionAnalyticsService;
import com.jkefbq.gymentry.service.database.SubscriptionService;
import com.jkefbq.gymentry.service.database.VisitAnalyticsService;
import com.jkefbq.gymentry.service.database.VisitService;
import com.jkefbq.gymentry.dto.statistics.PurchaseStatistics;
import com.jkefbq.gymentry.dto.statistics.VisitStatistics;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminStatisticsFacade {

    private final VisitService visitService;
    private final VisitAnalyticsService visitAnalyticsService;
    private final SubscriptionAnalyticsService subscriptionAnalyticsService;
    private final SubscriptionService subscriptionService;

    @Transactional
    public VisitStatistics getVisitStatisticsForPeriod(LocalDateTime from, LocalDateTime to, String gymAddress) {
        List<VisitDto> visitsForPeriod = visitService.getAllForPeriod(from, to, gymAddress);
        Long wholeDays = ChronoUnit.DAYS.between(from, to);
        return VisitStatistics.builder()
                .from(from)
                .to(to)
                .gymAddress(gymAddress)
                .totalVisits(visitsForPeriod.size())
                .avgPerDay(visitAnalyticsService.getAvgPerDay(visitsForPeriod.size(), wholeDays))
                .peakVisitsDay(visitAnalyticsService.getPeakDay(visitsForPeriod))
                .byDay(visitAnalyticsService.getVisitsPerDate(visitsForPeriod))
                .byTariffType(visitAnalyticsService.getTariffsPerDate(visitsForPeriod))
                .build();
    }

    @Transactional
    public PurchaseStatistics getPurchaseStatisticsForPeriod(LocalDate from, LocalDate to) {
        List<SubscriptionDto> subscriptionsForPeriod = subscriptionService.getAllForPeriod(from, to);
        Long wholeDays = ChronoUnit.DAYS.between(from, to);
        return PurchaseStatistics.builder()
                .from(from)
                .to(to)
                .totalPurchases(subscriptionsForPeriod.size())
                .totalRevenue(subscriptionAnalyticsService.getTotalRevenue(subscriptionsForPeriod))
                .avgDayCheck(subscriptionAnalyticsService.getAvgDayCheck(subscriptionsForPeriod, wholeDays))
                .avgPurchaseCheck(subscriptionAnalyticsService.getAvgPerPurchaseCheck(subscriptionsForPeriod))
                .peakPurchasesDay(subscriptionAnalyticsService.getPeakDay(subscriptionsForPeriod))
                .byDay(subscriptionAnalyticsService.getPurchasesPerDate(subscriptionsForPeriod))
                .byTariffType(subscriptionAnalyticsService.getPurchasesPerTariff(subscriptionsForPeriod))
                .build();
    }
}
