package com.jkefbq.gymentry.service;

import com.jkefbq.gymentry.exception.TimeoutActivationCodeException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class VerificationCodeServiceImpl implements VerificationCodeService {

    private static final int AUTH_CODE_LENGTH = 6;
    private static final String CACHE_NAMES = "auth_code";
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public String generateAndSaveVerificationCode(String email) {
        SecureRandom sr = new SecureRandom();
        String code = String.format("%06d", sr.nextInt(getCodeBound()));
        redisTemplate.opsForValue().set(CACHE_NAMES + "::" + email, code);
        return code;
    }

    @Override
    public boolean compareVerificationCode(String email, String code) throws TimeoutActivationCodeException {
        try {
            return redisTemplate.opsForValue().get(CACHE_NAMES + "::" + email).equals(code);
        } catch (NullPointerException e) {
            throw new TimeoutActivationCodeException("time to enter code has expired or incorrect");
        }
    }

    @Override
    public void deleteVerificationCode(String email) {
        redisTemplate.delete(CACHE_NAMES + "::" + email);
    }

    private int getCodeBound() {
        return (int) Math.pow(10, AUTH_CODE_LENGTH);
    }
}
