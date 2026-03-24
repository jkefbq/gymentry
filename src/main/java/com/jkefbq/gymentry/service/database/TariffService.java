package com.jkefbq.gymentry.service.database;

import com.jkefbq.gymentry.dto.for_entity.TariffDto;
import com.jkefbq.gymentry.dto.for_entity.TariffType;

import java.util.List;
import java.util.Optional;

public interface TariffService {
    List<TariffDto> getAll();
    Optional<TariffDto> getByType(TariffType type);
    List<TariffDto> saveAll(List<TariffDto> tariffList);
    TariffDto create(TariffDto tariffDto);
    void deleteAll(List<TariffDto> tariffs);
}