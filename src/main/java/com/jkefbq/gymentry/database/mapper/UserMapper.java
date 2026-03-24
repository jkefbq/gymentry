package com.jkefbq.gymentry.database.mapper;

import com.jkefbq.gymentry.dto.for_entity.PartialUserDto;
import com.jkefbq.gymentry.dto.for_entity.UserDto;
import com.jkefbq.gymentry.dto.for_entity.UserWithPassword;
import com.jkefbq.gymentry.database.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {SubscriptionMapper.class})
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto dto);
    User toEntity(UserWithPassword dto);
    User toEntity(PartialUserDto dto);
    PartialUserDto toPartialDto(User user);
    UserWithPassword toDtoWithPassword(User user);
    UserDto toFullDto(PartialUserDto partial);
}
