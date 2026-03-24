package com.jkefbq.gymentry.service;

import com.jkefbq.gymentry.dto.for_entity.PartialUserDto;
import com.jkefbq.gymentry.dto.for_entity.SubscriptionDto;
import com.jkefbq.gymentry.service.database.SubscriptionService;
import com.jkefbq.gymentry.service.database.UserService;
import com.jkefbq.gymentry.dto.statistics.PurchaseDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MessageConsumerImpl implements MessageConsumer {

    private final UserService userService;
    private final SubscriptionService subscriptionService;
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
        subscriptionService.sendCreateMessage(sub);
    }
}
