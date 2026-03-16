package com.jkefbq.gymentry.service;

import com.jkefbq.gymentry.dto.PurchaseDto;
import com.jkefbq.gymentry.props.YamlConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer implements MessageProducer {

    private final KafkaTemplate<@NonNull String,@NonNull Object> kafkaTemplate;
    private final YamlConfig yamlConfig;

    @Override
    public void sendSubscriptionPurchase(PurchaseDto purchase) {
        kafkaTemplate.send(
                yamlConfig.getKafka().getTopics().getSubscriptionPurchases(),
                purchase
        );
    }
}