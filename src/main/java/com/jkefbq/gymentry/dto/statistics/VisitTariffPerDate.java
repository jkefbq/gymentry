package com.jkefbq.gymentry.dto.statistics;

import com.jkefbq.gymentry.dto.for_entity.TariffType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VisitTariffPerDate {
    private TariffType tariffType;
    private Long count;
}
