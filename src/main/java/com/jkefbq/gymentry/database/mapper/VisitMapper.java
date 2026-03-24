package com.jkefbq.gymentry.database.mapper;

import com.jkefbq.gymentry.dto.for_entity.VisitDto;
import com.jkefbq.gymentry.database.entity.Visit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VisitMapper {
    VisitDto toDto(Visit entity);
    Visit toEntity(VisitDto dto);
}