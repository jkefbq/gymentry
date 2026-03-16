package com.jkefbq.gymentry.service;

import com.jkefbq.gymentry.dto.PurchaseDto;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface MessageConsumer {
    void consumeSuccessfullyPurchase(ConsumerRecord<String, PurchaseDto> record);
}
