package com.parcheggio.parcheggio_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class AuthenticationConfig {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationConfig.class);

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        logger.info("AuthenticationConfig inizializzato con UserDetailsService: {} e PasswordEncoder: {}", 
                   userDetailsService.getClass().getSimpleName(), 
                   passwordEncoder.getClass().getSimpleName());
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        logger.info("Configurazione del DaoAuthenticationProvider");
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        // Fa sì che le eccezioni di password vengano propagate correttamente
        provider.setHideUserNotFoundExceptions(false);
        logger.info("DaoAuthenticationProvider configurato con successo");
        return provider;
    }
}
