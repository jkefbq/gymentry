package com.jkefbq.gymentry.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PeakPurchasesDay {
    private LocalDate date;
    private Long purchaseCount;
    private BigDecimal purchaseSum;

    public PeakPurchasesDay(PurchasePerDate purchase) {
        this.date = purchase.getDate();
        this.purchaseCount = purchase.getPurchaseCount();
        this.purchaseSum = purchase.getPurchaseSum();
    }
}
