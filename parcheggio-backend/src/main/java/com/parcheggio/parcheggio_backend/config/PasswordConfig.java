package com.parcheggio.parcheggio_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class PasswordConfig {
    private static final Logger logger = LoggerFactory.getLogger(PasswordConfig.class);

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Configurazione dell'encoder per le password");
        return new BCryptPasswordEncoder(10); // Utilizziamo una strength più alta per maggiore sicurezza
    }
}
