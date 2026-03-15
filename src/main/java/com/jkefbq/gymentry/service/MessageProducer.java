package com.jkefbq.gymentry.service;

import com.jkefbq.gymentry.dto.PurchaseDto;

public interface MessageProducer {
    void sendSubscriptionPurchase(PurchaseDto purchase);
}
