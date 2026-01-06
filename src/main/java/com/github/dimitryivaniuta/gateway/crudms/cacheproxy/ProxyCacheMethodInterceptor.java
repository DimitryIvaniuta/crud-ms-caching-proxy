package com.github.dimitryivaniuta.gateway.crudms.cacheproxy;

import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * Intercepts method calls:
 * - If @ProxyCacheable present => cache result with TTL
 * - If @ProxyCacheEvict present => clear region(s) after successful call
 */
@RequiredArgsConstructor
public class ProxyCacheMethodInterceptor implements MethodInterceptor {

    private final TtlCacheStore cacheStore;
    private final long defaultTtlMs;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();

        ProxyCacheable cacheable = method.getAnnotation(ProxyCacheable.class);
        if (cacheable != null) {
            String methodId = method.getDeclaringClass().getName() + "#" + method.getName();
            CacheKey key = CacheKey.of(cacheable.cacheName(), methodId, invocation.getArguments());

            Object cached = cacheStore.get(key);
            if (cached != null) return cached;

            Object result = invocation.proceed();
            if (result != null) { // keep it simple: do not cache nulls
                long ttl = cacheable.ttlMs() > 0 ? cacheable.ttlMs() : defaultTtlMs;
                cacheStore.put(key, result, ttl);
            }
            return result;
        }

        ProxyCacheEvict evict = method.getAnnotation(ProxyCacheEvict.class);
        if (evict != null) {
            Object result = invocation.proceed(); // proceed first => evict only on success
            if (evict.allEntries()) {
                for (String region : evict.cacheNames()) cacheStore.clearRegion(region);
            }
            return result;
        }

        return invocation.proceed();
    }
}
