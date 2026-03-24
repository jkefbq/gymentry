package com.jkefbq.gymentry.database.service;

import com.jkefbq.gymentry.dto.for_entity.SubscriptionDto;
import com.jkefbq.gymentry.dto.for_entity.TariffType;
import com.jkefbq.gymentry.dto.statistics.PeakPurchasesDay;
import com.jkefbq.gymentry.dto.statistics.PurchasePerDate;
import com.jkefbq.gymentry.dto.statistics.PurchaseTariffTypePerDate;
import com.jkefbq.gymentry.service.database.SubscriptionAnalyticsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class SubscriptionAnalyticsServiceTest {

    @Spy
    SubscriptionAnalyticsServiceImpl subscriptionAnalytics;

    private static final BigDecimal DEF_SUB_SNAPSHOT_PRICE = BigDecimal.TEN;

    public SubscriptionDto getSubDto(boolean isActive) {
        return SubscriptionDto.builder()
                .id(UUID.randomUUID())
                .active(isActive)
                .snapshotPrice(DEF_SUB_SNAPSHOT_PRICE)
                .tariffType(TariffType.BASIC)
                .visitsLeft(5)
                .visitsTotal(10)
                .purchaseAt(LocalDate.now())
                .build();
    }

    @Test
    public void getAvgDayCheckTest() {
        var sub = getSubDto(false);
        sub.setSnapshotPrice(BigDecimal.TEN);
        List<SubscriptionDto> subs = List.of(sub, sub, sub);
        var wholeDays = 3L;

        BigDecimal avgDayCheck = subscriptionAnalytics.getAvgDayCheck(subs, wholeDays);

        assertEquals(BigDecimal.TEN, avgDayCheck);
    }

    @Test
    public void getPeakDayTest() {
        var target = getSubDto(false);
        target.setPurchaseAt(LocalDate.now().plusDays(3));
        target.setSnapshotPrice(BigDecimal.valueOf(99999999));
        var subs = List.of(target, getSubDto(true), getSubDto(false), getSubDto(false));

        PeakPurchasesDay peak = subscriptionAnalytics.getPeakDay(subs);

        assertEquals(target.getSnapshotPrice(), peak.getPurchaseSum());
        assertEquals(target.getPurchaseAt(), peak.getDate());
        assertEquals(1, peak.getPurchaseCount());
    }

    @Test
    public void getPurchasesPerDateTest() {
        var subs = List.of(getSubDto(true), getSubDto(false), getSubDto(false), getSubDto(false), getSubDto(false));
        var totalPrice = DEF_SUB_SNAPSHOT_PRICE.multiply(BigDecimal.valueOf(subs.size()));

        List<PurchasePerDate> purchases = subscriptionAnalytics.getPurchasesPerDate(subs);

        assertEquals(1, purchases.size());
        assertEquals(subs.getFirst().getPurchaseAt(), purchases.getFirst().getDate());
        assertEquals(totalPrice, purchases.getFirst().getPurchaseSum());
        assertEquals(subs.size(), purchases.getFirst().getPurchaseCount());
    }

    @Test
    public void getPurchasesPerTariffTest() {
        var subBasic = getSubDto(false);
        subBasic.setTariffType(TariffType.BASIC);
        var subPremium1 = getSubDto(false);
        subPremium1.setTariffType(TariffType.PREMIUM);
        var subPremium2 = getSubDto(false);
        subPremium2.setTariffType(TariffType.PREMIUM);
        var subGod = getSubDto(false);
        subGod.setTariffType(TariffType.GOD);
        var subs = List.of(subPremium1, subPremium2, subBasic, subGod);

        List<PurchaseTariffTypePerDate> purchases = subscriptionAnalytics.getPurchasesPerTariff(subs);
        var premiums = purchases.stream().filter(s -> s.getTariffType() == TariffType.PREMIUM).toList();

        assertEquals(subs.size() - 1, purchases.size());
        assertEquals(1, premiums.size());
        assertEquals(2, premiums.getFirst().getPurchaseCount());
        assertEquals(DEF_SUB_SNAPSHOT_PRICE.multiply(BigDecimal.TWO), premiums.getFirst().getPurchaseSum());
    }

    @Test
    public void getTotalRevenueTest() {
        var subs = List.of(getSubDto(true), getSubDto(false), getSubDto(false), getSubDto(false), getSubDto(false));
        var totalPrice = DEF_SUB_SNAPSHOT_PRICE.multiply(BigDecimal.valueOf(subs.size()));

        BigDecimal result = subscriptionAnalytics.getTotalRevenue(subs);

        assertEquals(totalPrice, result);
    }

    @Test
    public void getAvgPerPurchaseTest() {
        var subs = List.of(getSubDto(true), getSubDto(false), getSubDto(false), getSubDto(false), getSubDto(false));
        var totalPrice = DEF_SUB_SNAPSHOT_PRICE.multiply(BigDecimal.valueOf(subs.size()));
        var trueAvgPersonCheck = totalPrice.divide(BigDecimal.valueOf(subs.size()), RoundingMode.HALF_UP);

        BigDecimal resultAvgPersonCheck = subscriptionAnalytics.getAvgPerPurchaseCheck(subs);

        assertEquals(trueAvgPersonCheck, resultAvgPersonCheck);
    }
}
