package com.jkefbq.gymentry.database.entity;

import com.jkefbq.gymentry.dto.for_entity.TariffType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tariffs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tariff {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    private String tariffName;
    private String description;
    private BigDecimal pricePerLesson;
    @Enumerated(EnumType.STRING)
    private TariffType tariffType;
}
