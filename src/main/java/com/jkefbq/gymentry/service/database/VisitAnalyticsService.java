package com.jkefbq.gymentry.service.database;

import com.jkefbq.gymentry.dto.for_entity.VisitDto;
import com.jkefbq.gymentry.dto.statistics.PeakVisitsDay;
import com.jkefbq.gymentry.dto.statistics.VisitPerDate;
import com.jkefbq.gymentry.dto.statistics.VisitTariffPerDate;

import java.math.BigDecimal;
import java.util.List;

public interface VisitAnalyticsService {
    BigDecimal getAvgPerDay(Integer visitCount, Long wholeDays);
    List<VisitPerDate> getVisitsPerDate(List<VisitDto> visitsForPeriod);
    PeakVisitsDay getPeakDay(List<VisitDto> visitsForPeriod);
    List<VisitTariffPerDate> getTariffsPerDate(List<VisitDto> visitsForPeriod);
}
