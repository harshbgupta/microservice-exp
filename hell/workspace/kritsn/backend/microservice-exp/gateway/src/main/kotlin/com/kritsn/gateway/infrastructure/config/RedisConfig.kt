package com.kritsn.gateway.infrastructure.config

/**
 * Copyright © 2025 Kritsn LLP. All rights reserved.
 *
 * @author Radhey (hr-sh)
 * @since Sep 05, 2025
 */

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.core.StringRedisTemplate

@Configuration
class RedisConfig {

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        // Or use properties: spring.data.redis.host/port/password
        val cfg = RedisStandaloneConfiguration("localhost", 6379)
        return LettuceConnectionFactory(cfg)
    }

    @Bean
    fun stringRedisTemplate(cf: LettuceConnectionFactory): StringRedisTemplate =
        StringRedisTemplate(cf)
}