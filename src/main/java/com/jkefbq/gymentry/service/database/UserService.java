package com.jkefbq.gymentry.service.database;

import com.jkefbq.gymentry.dto.for_entity.PartialUserDto;
import com.jkefbq.gymentry.dto.for_entity.UserWithPassword;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    PartialUserDto create(UserWithPassword dto);
    Optional<PartialUserDto> findByEmail(String email);
    Optional<UserWithPassword> findWithPasswordByEmail(String email);
    boolean isCorrectEmailAndPassword(String email, String passwordToCheck);
    boolean existsByEmail(String email);
    PartialUserDto update(PartialUserDto user);
    Optional<PartialUserDto> findById(UUID userId);
    Optional<UUID> getUserIdByEmail(String email);
}
