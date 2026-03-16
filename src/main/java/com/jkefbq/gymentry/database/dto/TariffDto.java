package com.jkefbq.gymentry.database.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TariffDto implements Serializable {
    private UUID id;
    private String tariffName;
    private String description;
    private BigDecimal pricePerLesson;
    private TariffType tariffType;
}