package com.jkefbq.gymentry.database.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jkefbq.gymentry.dto.for_entity.TariffType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Subscription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    private Integer visitsTotal;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal snapshotPrice;
    private LocalDate purchaseAt;
    private Integer visitsLeft;
    @Column(name = "is_active")
    private Boolean active;
    @Enumerated(EnumType.STRING)
    private TariffType tariffType;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
