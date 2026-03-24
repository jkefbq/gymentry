package com.jkefbq.gymentry.security;

import com.jkefbq.gymentry.dto.auth.TokenPairDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAmount;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

    private static final TemporalAmount ACCESS_TTL = Duration.ofHours(1);
    private static final TemporalAmount REFRESH_TTL = Duration.ofDays(18);

    @Value("${app.auth.encryption-key}")
    private String jwtSecretEncryptionKey;

    @Override
    public TokenPairDto generateTokenPair(String email) {
        TokenPairDto pairDto = new TokenPairDto();
        pairDto.setAccessToken(generateAccessToken(email));
        pairDto.setRefreshToken(generateRefreshToken(email));
        return pairDto;
    }

    @Override
    public TokenPairDto refreshAccessTokenAndRotate(String email) {
        TokenPairDto pairDto = new TokenPairDto();
        pairDto.setAccessToken(generateAccessToken(email));
        pairDto.setRefreshToken(generateRefreshToken(email));
        return pairDto;
    }

    @Override
    public boolean isAnyTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    @Override
    public String generateAccessToken(String email) {
        Date date = Date.from(LocalDateTime.now().plus(ACCESS_TTL).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(email)
                .expiration(date)
                .signWith(getSecretKey())
                .compact();
    }

    @Override
    public String generateRefreshToken(String email) {
        Date date = Date.from(LocalDateTime.now().plus(REFRESH_TTL).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(email)
                .expiration(date)
                .signWith(getSecretKey())
                .compact();
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretEncryptionKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
