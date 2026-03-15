package com.jkefbq.gymentry.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KafkaTopics {
    SUBSCRIPTION_PURCHASES("subscription-purchases"),
    CONFIRMED_SUB_PURCHASES("confirmed-subscription-purchases");

    private String realName;
}
