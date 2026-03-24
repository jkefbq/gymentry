package com.jkefbq.gymentry.service.database;

import com.jkefbq.gymentry.dto.for_entity.NotVerifiedUserDto;

import java.util.Optional;

public interface NotVerifiedUserService {
    NotVerifiedUserDto create(NotVerifiedUserDto dto);
    boolean existsByEmail(String email);
    Optional<NotVerifiedUserDto> findUserByEmail(String email);
    void deleteByEmail(String email);
}
