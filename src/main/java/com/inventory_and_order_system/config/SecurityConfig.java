package com.inventory_and_order_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui/**", "/v3/api-docs/**", "/swagger/resources/**", "/swagger-resources"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeRequests -> {
                    for (String endpoint : SWAGGER_WHITELIST) {
                        authorizeRequests
                                .requestMatchers(new AntPathRequestMatcher(endpoint)).permitAll();
                    }
                    authorizeRequests.anyRequest().permitAll();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults());
                return http.build();
    }
}
