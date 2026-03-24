package com.jkefbq.gymentry.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class VisitPerDate {
    private LocalDate date;
    private Long visitCount;
}
