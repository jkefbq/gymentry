package com.jkefbq.gymentry.security;

import com.jkefbq.gymentry.dto.auth.TokenPairDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class JwtServiceImplTest {

    private static final String EMAIL = "email@gmail.com";
    static JwtServiceImpl jwtService = new JwtServiceImpl();

    @BeforeAll
    public static void setUp() throws NoSuchFieldException, IllegalAccessException {
        // set secret encrypt key
        Field field = jwtService.getClass().getDeclaredField("jwtSecretEncryptionKey");
        field.setAccessible(true);
        field.set(jwtService, RandomStringUtils.secure().nextAlphabetic(200, 300));
    }

    @Test
    public void generateTokenPairTest() {
        TokenPairDto tokenPair = jwtService.generateTokenPair(EMAIL);

        assertNotNull(tokenPair);
        assertNotNull(tokenPair.getAccessToken());
        assertNotNull(tokenPair.getRefreshToken());
        assertTrue(jwtService.isAnyTokenValid(tokenPair.getAccessToken()));
        assertTrue(jwtService.isAnyTokenValid(tokenPair.getRefreshToken()));
    }

    @Test
    public void refreshAccessTokenAndRotateTest() {
        TokenPairDto tokenPair = jwtService.refreshAccessTokenAndRotate(EMAIL);

        assertNotNull(tokenPair);
        assertNotNull(tokenPair.getAccessToken());
        assertNotNull(tokenPair.getRefreshToken());
        assertTrue(jwtService.isAnyTokenValid(tokenPair.getAccessToken()));
        assertTrue(jwtService.isAnyTokenValid(tokenPair.getRefreshToken()));
    }

    @Test
    public void isAnyTokenValidTest_valid() {
        String token = jwtService.generateAccessToken(EMAIL);

        boolean isValid = jwtService.isAnyTokenValid(token);

        assertTrue(isValid);
    }

    @Test
    public void isAnyTokenValidTest_invalid() {
        String token = jwtService.generateAccessToken(EMAIL) + "a";

        boolean isValid = jwtService.isAnyTokenValid(token);

        assertFalse(isValid);
    }

    @Test
    public void getEmailFromTokenTest() {
        var token = jwtService.generateAccessToken(EMAIL);

        String emailFromToken = jwtService.getEmailFromToken(token);

        assertEquals(EMAIL, emailFromToken);
    }

    @Test
    public void generateAccessTokenTest() {
        var token = jwtService.generateAccessToken(EMAIL);

        assertTrue(jwtService.isAnyTokenValid(token));
    }

    @Test
    public void generateRefreshTokenTest() {
        var token = jwtService.generateRefreshToken(EMAIL);

        assertTrue(jwtService.isAnyTokenValid(token));
    }
}
