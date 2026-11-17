package com.polarbookshop.catalogservice.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableJdbcAuditing
public class DataConfig {

    @Bean
    AuditorAware<String> auditorAware() {
        return () -> Optional.ofNullable(
                        SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .filter(authentication ->
                        !(authentication instanceof AnonymousAuthenticationToken))
                .map(Authentication::getName)
                .filter(name -> !name.isBlank())
                .or(() -> Optional.of("unknown"));
    }
}
