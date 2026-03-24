package com.jkefbq.gymentry.service;

import com.jkefbq.gymentry.dto.statistics.PurchaseDto;

public interface MessageProducer {
    void sendSubscriptionPurchase(PurchaseDto purchase);
}
