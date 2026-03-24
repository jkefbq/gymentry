package com.jkefbq.gymentry.dto.statistics;

import com.jkefbq.gymentry.dto.for_entity.TariffType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PurchaseTariffTypePerDate {
    private TariffType tariffType;
    private Long purchaseCount;
    private BigDecimal purchaseSum;
}
