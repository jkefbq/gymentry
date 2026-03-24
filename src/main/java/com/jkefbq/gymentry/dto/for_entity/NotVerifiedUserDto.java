package com.jkefbq.gymentry.dto.for_entity;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotVerifiedUserDto {
    private UUID id;
    private String firstName;
    @Email
    private String email;
    private String password;
}