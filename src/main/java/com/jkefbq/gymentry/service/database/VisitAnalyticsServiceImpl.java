package com.jkefbq.gymentry.service.database;

import com.jkefbq.gymentry.dto.for_entity.VisitDto;
import com.jkefbq.gymentry.dto.statistics.PeakVisitsDay;
import com.jkefbq.gymentry.dto.statistics.VisitPerDate;
import com.jkefbq.gymentry.dto.statistics.VisitTariffPerDate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@PreAuthorize("hasRole('ADMIN')")
public class VisitAnalyticsServiceImpl implements VisitAnalyticsService {

    @Override
    public BigDecimal getAvgPerDay(Integer visitCount, Long wholeDays) {
        return BigDecimal.valueOf(visitCount)
                .divide(
                        BigDecimal.valueOf(wholeDays),
                        RoundingMode.HALF_UP
                );
    }

    @Override
    public List<VisitPerDate> getVisitsPerDate(List<VisitDto> visitsForPeriod) {
        return visitsForPeriod.stream()
                .map(visit -> visit.getCreatedAt().toLocalDate())
                .collect(Collectors.toMap(
                        visit -> visit,
                        visit -> 1L,
                        Long::sum
                )).entrySet().stream()
                .map(entry -> new VisitPerDate(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(VisitPerDate::getDate))
                .toList();
    }

    @Override
    public PeakVisitsDay getPeakDay(List<VisitDto> visitsForPeriod) {
        List<VisitPerDate> visitsPerDates = getVisitsPerDate(visitsForPeriod);
        return visitsPerDates.stream()
                .max(Comparator.comparing(VisitPerDate::getVisitCount))
                .map(visit -> new PeakVisitsDay(visit.getDate(), visit.getVisitCount()))
                .orElseThrow(() -> new IllegalStateException("result array is empty"));
    }

    @Override
    public List<VisitTariffPerDate> getTariffsPerDate(List<VisitDto> visitsForPeriod) {
        return visitsForPeriod.stream()
                .map(visit -> visit.getSubscription().getTariffType())
                .collect(Collectors.toMap(
                        tariff -> tariff,
                        tariff -> 1L,
                        Long::sum
                )).entrySet().stream()
                .map(entry -> new VisitTariffPerDate(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(VisitTariffPerDate::getCount))
                .toList();
    }
}
