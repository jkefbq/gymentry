package com.jkefbq.gymentry.database.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VisitDto implements Serializable {
    private UUID id;
    private LocalDateTime createdAt;
    private GymInfoDto gym;
    private SubscriptionDto subscription;
}