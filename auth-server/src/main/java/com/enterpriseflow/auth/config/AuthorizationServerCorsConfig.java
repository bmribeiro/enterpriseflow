package com.enterpriseflow.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class AuthorizationServerCorsConfig {

    @Bean
    public FilterRegistrationBean<CorsFilter> authorizationServerCorsFilter(
            @Value("${app.cors.allowed-origins:http://localhost:4200}")
            String allowedOrigins) {

        CorsConfiguration cors = new CorsConfiguration();

        cors.setAllowedOrigins(
                List.of(allowedOrigins.split("\\s*,\\s*"))
        );

        cors.setAllowedMethods(
                List.of("GET", "POST", "OPTIONS")
        );

        cors.setAllowedHeaders(
                List.of("Content-Type")
        );

        cors.setAllowCredentials(false);
        cors.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/.well-known/**", cors
        );

        source.registerCorsConfiguration(
                "/oauth2/token", cors
        );

        source.registerCorsConfiguration(
                "/oauth2/jwks", cors
        );

        FilterRegistrationBean<CorsFilter> registration =
                new FilterRegistrationBean<>(
                        new CorsFilter(source)
                );

        registration.setOrder(
                Ordered.HIGHEST_PRECEDENCE
        );

        return registration;
    }
}
