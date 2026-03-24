package com.jkefbq.gymentry.database.repository;

import com.jkefbq.gymentry.database.entity.Tariff;
import com.jkefbq.gymentry.dto.for_entity.TariffType;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TariffRepository extends JpaRepository<@NonNull Tariff, @NonNull UUID> {
    Optional<Tariff> getByTariffType(TariffType tariffType);
}
