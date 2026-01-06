package com.github.dimitryivaniuta.gateway.crudms.cacheproxy;

import java.util.Arrays;

public record CacheKey(String cacheName, String method, int argsHash) {
    public static CacheKey of(String cacheName, String method, Object[] args) {
        return new CacheKey(cacheName, method, Arrays.deepHashCode(args));
    }
}
