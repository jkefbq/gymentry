package com.jkefbq.gymentry.dto.for_entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionRequestDto {
    private Integer visitsTotal;
    private TariffType tariffType;
}
