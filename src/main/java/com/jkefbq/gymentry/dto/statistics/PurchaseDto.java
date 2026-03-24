package com.jkefbq.gymentry.dto.statistics;

import com.jkefbq.gymentry.dto.auth.EmailDto;
import com.jkefbq.gymentry.dto.for_entity.TariffType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseDto {
    private Integer visitsTotal;
    private TariffType tariffType;
    private EmailDto ownerEmail;
}
