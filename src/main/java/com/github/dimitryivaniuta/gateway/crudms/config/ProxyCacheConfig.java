package com.github.dimitryivaniuta.gateway.crudms.config;

import com.github.dimitryivaniuta.gateway.crudms.cacheproxy.ProxyCacheBeanPostProcessor;
import com.github.dimitryivaniuta.gateway.crudms.cacheproxy.TtlCacheStore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "proxy-cache")
record ProxyCacheProps(boolean enabled, long defaultTtlMs) {}

@Configuration
@EnableConfigurationProperties(ProxyCacheProps.class)
public class ProxyCacheConfig {

    @Bean
    public ProxyCacheBeanPostProcessor proxyCacheBeanPostProcessor(
            ProxyCacheProps props,
            TtlCacheStore store
    ) {
        if (!props.enabled()) {
            // Return a “no-op” post processor (no proxies created).
            return new ProxyCacheBeanPostProcessor(store, props.defaultTtlMs()) {
                @Override public Object postProcessAfterInitialization(Object bean, String beanName) { return bean; }
            };
        }
        return new ProxyCacheBeanPostProcessor(store, props.defaultTtlMs());
    }
}
