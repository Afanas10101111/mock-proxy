package com.github.afanas10101111.mp.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Setter
@Configuration
@ConfigurationProperties(prefix = "proxy")
class ProxyConfig {
    private static final String ROUTE_ID = "main";

    private String path;
    private String url;

    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(ROUTE_ID, route -> route
                        .path(path)
                        .and()
                        .readBody(String.class, StringUtils::hasText)
                        .uri(url))
                .build();
    }
}
