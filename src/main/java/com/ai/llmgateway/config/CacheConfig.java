package com.ai.llmgateway.config;


import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {


        @Bean
        public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {

            GenericJacksonJsonRedisSerializer serializer = GenericJacksonJsonRedisSerializer.builder().build();

            RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                            .entryTtl(Duration.ofMinutes(10))
                            .disableCachingNullValues()
                            .serializeKeysWith(
                                    RedisSerializationContext.SerializationPair.fromSerializer(
                                            new StringRedisSerializer()))
                            .serializeValuesWith(
                                    RedisSerializationContext.SerializationPair.fromSerializer(
                                            serializer));

            return RedisCacheManager.builder(connectionFactory)
                    .cacheDefaults(cacheConfiguration)
                    .transactionAware()
                    .build();
        }
    }
/**
    @Bean
    public CacheManager cacheManager(ReactiveRedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))

                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder((RedisCacheWriter) redisConnectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }
    }
**/



