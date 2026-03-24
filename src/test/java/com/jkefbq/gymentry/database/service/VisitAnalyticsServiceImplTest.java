package com.jkefbq.gymentry.database.service;

import com.jkefbq.gymentry.dto.for_entity.SubscriptionDto;
import com.jkefbq.gymentry.dto.for_entity.TariffType;
import com.jkefbq.gymentry.dto.for_entity.VisitDto;
import com.jkefbq.gymentry.dto.statistics.PeakVisitsDay;
import com.jkefbq.gymentry.dto.statistics.VisitPerDate;
import com.jkefbq.gymentry.dto.statistics.VisitTariffPerDate;
import com.jkefbq.gymentry.service.database.VisitAnalyticsService;
import com.jkefbq.gymentry.service.database.VisitAnalyticsServiceImpl;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VisitAnalyticsServiceImplTest {

    VisitAnalyticsService visitAnalyticsService = new VisitAnalyticsServiceImpl();

    @Test
    public void getAvgPerDayTest() {
        var visitCount = 500;
        var wholeDays = 25L;
        BigDecimal trueResult = BigDecimal.valueOf(visitCount / wholeDays);

        BigDecimal res = visitAnalyticsService.getAvgPerDay(visitCount, wholeDays);

        assertEquals(trueResult, res);
    }

    @Test
    public void getVisitsPerDateTest() {
        var v1 = VisitDto.builder().createdAt(LocalDateTime.now()).build();
        var v2 = VisitDto.builder().createdAt(LocalDateTime.now()).build();
        var v3 = VisitDto.builder().createdAt(LocalDateTime.now().plusDays(1)).build();
        var v4 = VisitDto.builder().createdAt(LocalDateTime.now().plusDays(2)).build();
        var v5 = VisitDto.builder().createdAt(LocalDateTime.now().plusDays(2)).build();
        List<VisitDto> visits = List.of(v1, v2, v3, v4, v5);

        List<VisitPerDate> resultList = visitAnalyticsService.getVisitsPerDate(visits);

        var mergedV1V2 = resultList.stream().filter(e -> e.getDate().equals(v1.getCreatedAt().toLocalDate())).findFirst().orElseThrow();
        assertEquals(3, resultList.size());
        assertEquals(2, mergedV1V2.getVisitCount());
    }

    @Test
    public void getPeakDayTest() {
        var targetVisit = VisitDto.builder().createdAt(LocalDateTime.now().plusDays(1)).build();
        var regularVisit = VisitDto.builder().createdAt(LocalDateTime.now()).build();
        var visits = List.of(regularVisit, targetVisit, targetVisit, targetVisit);

        PeakVisitsDay peak = visitAnalyticsService.getPeakDay(visits);

        assertEquals(targetVisit.getCreatedAt().toLocalDate(), peak.getDate());
        assertEquals(3, peak.getVisitCount());
    }

    @Test
    public void getTariffsPerDateTest() {
        var visitBasicTariff = VisitDto.builder()
                .subscription(SubscriptionDto.builder().tariffType(TariffType.BASIC).build())
                .build();
        var visitPremiumTariff = VisitDto.builder()
                .subscription(SubscriptionDto.builder().tariffType(TariffType.PREMIUM).build())
                .build();
        var visits = List.of(visitPremiumTariff, visitPremiumTariff, visitPremiumTariff, visitBasicTariff);

        List<VisitTariffPerDate> visitsByTariff = visitAnalyticsService.getTariffsPerDate(visits);

        var premium = visitsByTariff.stream().filter(e -> e.getTariffType() == TariffType.PREMIUM).findFirst().orElseThrow();
        assertEquals(2, visitsByTariff.size());
        assertEquals(3, premium.getCount());
    }
}
