package com.jkefbq.gymentry.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class EntryCodeServiceImpl implements EntryCodeService {

    private static final Duration CODE_TTL = Duration.ofMinutes(5);
    private static final String CACHE_NAMES = "entry_code";
    private final RedisTemplate<String, Object> redisTemplate;

    public String generateUserEntryCode(String email) {
        String code = RandomStringUtils.secureStrong().nextNumeric(6);
        redisTemplate.opsForValue().set(CACHE_NAMES + "::" + code, email, CODE_TTL);
        return code;
    }

    public String getEmailByCode(String code) {
        return (String) redisTemplate.opsForValue().get(CACHE_NAMES + "::" + code);
    }

}
