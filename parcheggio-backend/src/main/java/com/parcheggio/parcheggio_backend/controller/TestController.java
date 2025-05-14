package com.parcheggio.parcheggio_backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller per le operazioni di test dell'autenticazione
 */
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);
    
    /**
     * Endpoint per testare l'autenticazione. Richiede un token JWT valido.
     * @return Un messaggio di successo con il nome utente autenticato
     */
    @GetMapping("/auth")
    public ResponseEntity<?> testAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Test autenticazione per utente: {}", auth.getName());
        
        return ResponseEntity.ok(Map.of(
            "message", "Autenticazione riuscita",
            "user", auth.getName()
        ));
    }
    
    /**
     * Endpoint pubblico di test che non richiede autenticazione
     * @return Un messaggio di successo semplice
     */
    @GetMapping("/public")
    public ResponseEntity<?> testPublic() {
        logger.info("Test endpoint pubblico");
        
        return ResponseEntity.ok(Map.of(
            "message", "Endpoint pubblico accessibile"
        ));
    }
}
