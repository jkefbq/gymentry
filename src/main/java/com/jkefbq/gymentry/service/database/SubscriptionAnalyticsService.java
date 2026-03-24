package com.jkefbq.gymentry.service.database;

import com.jkefbq.gymentry.dto.for_entity.SubscriptionDto;
import com.jkefbq.gymentry.dto.statistics.PeakPurchasesDay;
import com.jkefbq.gymentry.dto.statistics.PurchasePerDate;
import com.jkefbq.gymentry.dto.statistics.PurchaseTariffTypePerDate;

import java.math.BigDecimal;
import java.util.List;

public interface SubscriptionAnalyticsService {
    BigDecimal getAvgDayCheck(List<SubscriptionDto> subscriptionsForPeriod, Long wholeDays);
    PeakPurchasesDay getPeakDay(List<SubscriptionDto> subscriptionsForPeriod);
    List<PurchasePerDate> getPurchasesPerDate(List<SubscriptionDto> subscriptionsForPeriod);
    List<PurchaseTariffTypePerDate> getPurchasesPerTariff(List<SubscriptionDto> subscriptionsForPeriod);
    BigDecimal getTotalRevenue(List<SubscriptionDto> subscriptionsForPeriod);
    BigDecimal getAvgPerPurchaseCheck(List<SubscriptionDto> subscriptionsForPeriod);
}
