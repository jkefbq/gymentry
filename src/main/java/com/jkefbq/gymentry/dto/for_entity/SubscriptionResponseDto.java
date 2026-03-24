package com.jkefbq.gymentry.dto.for_entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SubscriptionResponseDto {
    private Integer visitsTotal;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal snapshotPrice;
    private LocalDate purchaseAt;
    private Integer visitsLeft;
    private TariffType tariffType;
}