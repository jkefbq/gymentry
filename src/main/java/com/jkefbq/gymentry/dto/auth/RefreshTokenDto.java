package com.jkefbq.gymentry.dto.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RefreshTokenDto {
    private String refreshToken;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public RefreshTokenDto(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}