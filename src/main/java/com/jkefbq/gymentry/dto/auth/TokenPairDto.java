package com.jkefbq.gymentry.dto.auth;

import lombok.Data;

@Data
public class TokenPairDto {
    private String accessToken;
    private String refreshToken;
}
