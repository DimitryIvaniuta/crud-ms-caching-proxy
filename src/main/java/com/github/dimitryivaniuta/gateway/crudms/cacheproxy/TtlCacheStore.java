package com.github.dimitryivaniuta.gateway.crudms.cacheproxy;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory TTL cache.
 * - Regions are separated by cacheName
 * - Each entry has expireAtMillis
 */
@Component
public class TtlCacheStore {

    private final Clock clock = Clock.systemUTC();
    private final Map<String, Map<CacheKey, CacheEntry>> regions = new ConcurrentHashMap<>();

    public Object get(CacheKey key) {
        Map<CacheKey, CacheEntry> region = regions.get(key.cacheName());
        if (region == null) return null;

        CacheEntry entry = region.get(key);
        if (entry == null) return null;

        long now = clock.millis();
        if (entry.expireAtMillis <= now) {
            region.remove(key);
            return null;
        }
        return entry.value;
    }

    public void put(CacheKey key, Object value, long ttlMs) {
        long expireAt = clock.millis() + ttlMs;
        regions.computeIfAbsent(key.cacheName(), __ -> new ConcurrentHashMap<>())
                .put(key, new CacheEntry(value, expireAt));
    }

    public void clearRegion(String cacheName) {
        Map<CacheKey, CacheEntry> region = regions.get(cacheName);
        if (region != null) region.clear();
    }

    private record CacheEntry(Object value, long expireAtMillis) {}
}
