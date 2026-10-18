package com.enterpriseflow.auth.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

@Configuration
public class JwtTokenCustomizerConfig {

    private static final String API_AUDIENCE =
            "enterpriseflow-api";

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext>
    jwtTokenCustomizer() {

        return context -> {

            if (OAuth2TokenType.ACCESS_TOKEN.equals(
                    context.getTokenType())) {

                context.getClaims().audience(
                        List.of(API_AUDIENCE)
                );
            }
        };
    }
}
