package com.github.dimitryivaniuta.gateway.crudms.cacheproxy;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProxyCacheEvict {
    /**
     * One or more cache regions to clear after method success.
     */
    String[] cacheNames();

    /**
     * If true, clears full region(s). (Recommended for CRUD writes.)
     */
    boolean allEntries() default true;
}
