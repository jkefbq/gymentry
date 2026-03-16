package com.jkefbq.gymentry.service;

import com.jkefbq.gymentry.database.dto.PartialUserDto;
import com.jkefbq.gymentry.database.dto.SubscriptionDto;
import com.jkefbq.gymentry.database.service.SubscriptionManager;
import com.jkefbq.gymentry.database.service.UserService;
import com.jkefbq.gymentry.dto.PurchaseDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class KafkaConsumer implements MessageConsumer {

    private final UserService userService;
    private final SubscriptionManager subscriptionManager;
    private final SubscriptionPriceCalculator subscriptionPriceCalculator;

    @KafkaListener(topics = "${app.kafka.topics.confirmed-subscriptions}")
    @Override
    public void consumeSuccessfullyPurchase(ConsumerRecord<String, PurchaseDto> record) {
        PartialUserDto user = userService.findByEmail(record.value().getOwnerEmail().getEmail()).orElseThrow();
        SubscriptionDto sub = SubscriptionDto.builder()
                .visitsTotal(record.value().getVisitsTotal())
                .visitsLeft(record.value().getVisitsTotal())
                .tariffType(record.value().getTariffType())
                .active(false)
                .snapshotPrice(subscriptionPriceCalculator.calculate(record.value().getTariffType(), record.value().getVisitsTotal()))
                .purchaseAt(LocalDate.now())
                .userId(user.getId())
                .build();
        subscriptionManager.create(sub);
    }
}
