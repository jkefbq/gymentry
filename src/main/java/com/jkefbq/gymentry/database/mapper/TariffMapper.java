package com.jkefbq.gymentry.database.mapper;

import com.jkefbq.gymentry.dto.for_entity.TariffDto;
import com.jkefbq.gymentry.database.entity.Tariff;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TariffMapper {
    TariffDto toDto(Tariff tariff);
    Tariff toEntity(TariffDto dto);
}
