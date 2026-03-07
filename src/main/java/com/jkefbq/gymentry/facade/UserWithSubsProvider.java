package com.jkefbq.gymentry.facade;

import com.jkefbq.gymentry.database.dto.UserDto;

import java.util.UUID;

public interface UserWithSubsProvider {
    UserDto findByEmail(String email);
    UserDto findByUserId(UUID userId);
}
