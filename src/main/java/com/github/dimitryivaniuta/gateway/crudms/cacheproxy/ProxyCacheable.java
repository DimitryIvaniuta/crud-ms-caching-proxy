package com.github.dimitryivaniuta.gateway.crudms.cacheproxy;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProxyCacheable {
    /**
     * Cache “region” name. Example: "users", "contacts".
     */
    String cacheName();

    /**
     * Optional TTL override per method. If <= 0, default TTL is used.
     */
    long ttlMs() default -1;
}
