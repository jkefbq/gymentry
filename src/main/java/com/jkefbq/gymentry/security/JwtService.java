package com.jkefbq.gymentry.security;

import com.jkefbq.gymentry.dto.auth.TokenPairDto;

public interface JwtService {
    TokenPairDto generateTokenPair(String email);
    TokenPairDto refreshAccessTokenAndRotate(String email);
    boolean isAnyTokenValid(String refreshToken);
    String getEmailFromToken(String token);

    String generateAccessToken(String email);

    String generateRefreshToken(String email);
}
