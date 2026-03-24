package com.jkefbq.gymentry.service.database;

import com.jkefbq.gymentry.dto.for_entity.SubscriptionDto;
import com.jkefbq.gymentry.dto.statistics.PeakPurchasesDay;
import com.jkefbq.gymentry.dto.statistics.PurchasePerDate;
import com.jkefbq.gymentry.dto.statistics.PurchaseTariffTypePerDate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionAnalyticsServiceImpl implements SubscriptionAnalyticsService {

    @Override
    public BigDecimal getAvgDayCheck(List<SubscriptionDto> subscriptionsForPeriod, Long wholeDays) {
        return subscriptionsForPeriod.stream()
                .map(SubscriptionDto::getSnapshotPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(wholeDays), RoundingMode.HALF_UP);
    }

    @Override
    public PeakPurchasesDay getPeakDay(List<SubscriptionDto> subscriptionsForPeriod) {
        List<PurchasePerDate> purchasePerDates = getPurchasesPerDate(subscriptionsForPeriod);
        return purchasePerDates.stream()
                .max(Comparator.comparing(PurchasePerDate::getPurchaseSum))
                .map(PeakPurchasesDay::new)
                .orElseThrow(() -> new IllegalStateException("array of purchases is empty"));
    }

    @Override
    public List<PurchasePerDate> getPurchasesPerDate(List<SubscriptionDto> subscriptionsForPeriod) {
        return subscriptionsForPeriod.stream()
                .collect(Collectors.toMap(
                        SubscriptionDto::getPurchaseAt,
                        sub -> new PurchasePerDate(sub.getPurchaseAt(), 1L, sub.getSnapshotPrice()),
                        (purchase1, purchase2) -> {
                            purchase1.setPurchaseCount(purchase1.getPurchaseCount() + 1L);
                            purchase1.setPurchaseSum(purchase1.getPurchaseSum().add(purchase2.getPurchaseSum()));
                            return purchase1;
                        }
                )).values().stream()
                .sorted(Comparator.comparing(PurchasePerDate::getDate))
                .toList();
    }

    @Override
    public List<PurchaseTariffTypePerDate> getPurchasesPerTariff(List<SubscriptionDto> subscriptionsForPeriod) {
        return subscriptionsForPeriod.stream()
                .collect(Collectors.toMap(
                        SubscriptionDto::getTariffType,
                        sub -> new PurchaseTariffTypePerDate(sub.getTariffType(), 1L, sub.getSnapshotPrice()),
                        (purchase1, purchase2) -> {
                            purchase1.setPurchaseCount(purchase1.getPurchaseCount() + 1L);
                            purchase1.setPurchaseSum(purchase1.getPurchaseSum().add(purchase2.getPurchaseSum()));
                            return purchase1;
                        }
                )).values().stream().toList();
    }

    @Override
    public BigDecimal getTotalRevenue(List<SubscriptionDto> subscriptionsForPeriod) {
        return subscriptionsForPeriod.stream()
                .map(SubscriptionDto::getSnapshotPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getAvgPerPurchaseCheck(List<SubscriptionDto> subscriptionsForPeriod) {
        return subscriptionsForPeriod.stream()
                .map(SubscriptionDto::getSnapshotPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(subscriptionsForPeriod.size()), RoundingMode.HALF_UP);
    }
}
