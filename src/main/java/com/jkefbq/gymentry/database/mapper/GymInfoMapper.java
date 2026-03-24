package com.jkefbq.gymentry.database.mapper;

import com.jkefbq.gymentry.dto.for_entity.GymInfoDto;
import com.jkefbq.gymentry.database.entity.GymInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GymInfoMapper {
    GymInfoDto toDto(GymInfo entity);
    GymInfo toEntity(GymInfoDto dto);
}
