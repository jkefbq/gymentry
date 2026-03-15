package com.jkefbq.gymentry.service;

import com.jkefbq.gymentry.config.KafkaTopics;
import com.jkefbq.gymentry.dto.PurchaseDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer implements MessageProducer {

    private final KafkaTemplate<@NonNull String,@NonNull Object> kafkaTemplate;

    @Override
    public void sendSubscriptionPurchase(PurchaseDto purchase) {
        kafkaTemplate.send(
                KafkaTopics.SUBSCRIPTION_PURCHASES.getRealName(),
                purchase
        );
    }
}
