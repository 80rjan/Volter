package com.volter.shared.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Cache configuration. Caching itself is enabled by {@code @EnableCaching} on the
 * application class; this just gives named caches their expiry policy (Spring's
 * {@code @Cacheable} has no TTL of its own — it lives on the cache provider).
 */
@Configuration
public class CacheConfig {

    /** Gold price proxied from goldapi.io; refreshed at most once per hour. */
    public static final String GOLD_PRICE_CACHE = "goldPrice";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.registerCustomCache(
                GOLD_PRICE_CACHE,
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofHours(3))
                        .maximumSize(1)
                        .build()
        );
        return manager;
    }
}
