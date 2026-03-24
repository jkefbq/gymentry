package com.jkefbq.gymentry.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitStatistics {
    private LocalDateTime from;
    private LocalDateTime to;
    private String gymAddress;
    private Integer totalVisits;
    private BigDecimal avgPerDay;
    private PeakVisitsDay peakVisitsDay;
    private List<VisitPerDate> byDay;
    private List<VisitTariffPerDate> byTariffType;
}
