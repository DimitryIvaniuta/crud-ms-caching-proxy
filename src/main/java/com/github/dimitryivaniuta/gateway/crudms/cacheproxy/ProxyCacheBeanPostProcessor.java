package com.github.dimitryivaniuta.gateway.crudms.cacheproxy;

import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;

/**
 * Wraps beans with a proxy if ANY method has @ProxyCacheable or @ProxyCacheEvict.
 *
 * IMPORTANT:
 * - Works best when your services are interfaces (JDK proxy).
 * - If not, Spring can still proxy classes (CGLIB) via ProxyFactory.
 */
@RequiredArgsConstructor
public class ProxyCacheBeanPostProcessor implements BeanPostProcessor {

    private final TtlCacheStore cacheStore;
    private final long defaultTtlMs;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        if (!hasCachingAnnotations(targetClass)) return bean;

        ProxyFactory factory = new ProxyFactory(bean);
        factory.addAdvice(new ProxyCacheMethodInterceptor(cacheStore, defaultTtlMs));
        return factory.getProxy();
    }

    private boolean hasCachingAnnotations(Class<?> targetClass) {
        for (Method m : targetClass.getMethods()) {
            if (m.isAnnotationPresent(ProxyCacheable.class) || m.isAnnotationPresent(ProxyCacheEvict.class)) {
                return true;
            }
        }
        return false;
    }
}
