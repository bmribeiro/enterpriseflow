package com.enterpriseflow.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // Public diagnostic endpoints
                        .requestMatchers(
                                "/actuator/health",
                                "/api/v1/hello"
                        ).permitAll()

                        // Read operations
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/tenants/**"
                        ).hasAuthority("SCOPE_projects.read")

                        // Write operations
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/tenants/**"
                        ).hasAuthority("SCOPE_projects.write")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/tenants/**"
                        ).hasAuthority("SCOPE_projects.write")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/tenants/**"
                        ).hasAuthority("SCOPE_projects.write")

                        // Deny other requests
                        .anyRequest().denyAll()
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(Customizer.withDefaults())
                );

        return http.build();
    }
}