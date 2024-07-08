package com.solucitation.midpoint_backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.host}")
    private String host;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    // RefreshToken 저장을 위한 redisTemplate 설정
    @Bean(name = "tokenRedisTemplate")
    public RedisTemplate<String, String> tokenRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> tokenRedisTemplate = new RedisTemplate<>();
        tokenRedisTemplate.setConnectionFactory(connectionFactory);
        tokenRedisTemplate.setKeySerializer(new StringRedisSerializer());
        tokenRedisTemplate.setValueSerializer(new StringRedisSerializer());
        return tokenRedisTemplate;
    }
}
