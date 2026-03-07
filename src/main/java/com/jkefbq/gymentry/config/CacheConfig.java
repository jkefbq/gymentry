package com.jkefbq.gymentry.config;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jkefbq.gymentry.dto.CanCache;
import lombok.NonNull;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    private static final Duration ALL_ENTRY_TTL = Duration.ofMinutes(2);

    @Bean
    public ObjectMapper redisObjectMapper() {
        var objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        new ClassPathScanningCandidateComponentProvider(false) {{
            addIncludeFilter(new AnnotationTypeFilter(CanCache.class));
        }}
                .findCandidateComponents("com.jkefbq.gymentry")
                .forEach(bean -> {
                    try {
                        Class<?> clazz = Class.forName(bean.getBeanClassName());
                        objectMapper.addMixIn(clazz, TypeInfoMixin.class);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
        return objectMapper;
    }

    @Bean
    public RedisSerializer<@NonNull Object> redisValueSerializer(ObjectMapper redisObjectMapper) {
        return new EnvelopeRedisSerializer(redisObjectMapper);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory redisConnectionFactory,
            RedisSerializer<@NonNull Object> jsonSerializer
    ) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jsonSerializer);
        redisTemplate.setHashValueSerializer(jsonSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(RedisSerializer<@NonNull Object> redisValueSerializer) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ALL_ENTRY_TTL)
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisValueSerializer));
    }

    @Bean
    @Primary
    public RedisCacheManager cacheManager(
            RedisConnectionFactory redisConnectionFactory,
            RedisCacheConfiguration cacheConfiguration
    ) {
//        Map<String, RedisCacheConfiguration> cacheConfigs = Map.of(
//                "obj", cacheConfiguration.entryTtl(Duration.ofMinutes(1))
//        );
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration)
//                .withInitialCacheConfigurations(cacheConfigs)
                .transactionAware()
                .build();
    }
}
