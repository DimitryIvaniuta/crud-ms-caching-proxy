package com.github.dimitryivaniuta.gateway.crudms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "proxy-cache")
public record ProxyCacheProps(boolean enabled, long defaultTtlMs) {
}