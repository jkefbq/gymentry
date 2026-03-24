package com.jkefbq.gymentry.dto.for_entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionDto implements Serializable {
    private UUID id;
    private Integer visitsTotal;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal snapshotPrice;
    private LocalDate purchaseAt;
    private Integer visitsLeft;
    private TariffType tariffType;
    private UUID userId;
    private Boolean active;
}