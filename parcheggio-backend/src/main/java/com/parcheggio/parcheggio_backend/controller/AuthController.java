package com.parcheggio.parcheggio_backend.controller;

import com.parcheggio.parcheggio_backend.dto.LoginDto;
import com.parcheggio.parcheggio_backend.dto.RegisterDto;
import com.parcheggio.parcheggio_backend.dto.UpdatePasswordDto;
import com.parcheggio.parcheggio_backend.dto.VerifyEmailDto;
import com.parcheggio.parcheggio_backend.model.Users;
import com.parcheggio.parcheggio_backend.service.UsersService;
import com.parcheggio.parcheggio_backend.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authManager;
    private final UsersService userService;
    private final JwtTokenUtil jwtUtil;

    public AuthController(AuthenticationManager authManager,
                          UsersService userService,
                          JwtTokenUtil jwtUtil) {
        this.authManager = authManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        logger.info("AuthController inizializzato");
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto dto) {
        try {
            Users u = userService.register(dto);
            return ResponseEntity.ok(Map.of(
                "message", "Utente registrato con successo",
                "email", u.getEmail()
            ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto) {
        try {
            // Logging per il tentativo di login
            logger.info("Tentativo di login con email: {}", dto.getEmail());
            
            if (dto.getEmail() == null || dto.getEmail().isEmpty() || dto.getPassword() == null || dto.getPassword().isEmpty()) {
                logger.warn("Tentativo di login con credenziali mancanti per email: {}", dto.getEmail());
                return ResponseEntity.status(400).body(Map.of(
                    "error", "Email e password sono richiesti"
                ));
            }
            
            Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
            
            logger.info("Autenticazione riuscita per utente: {}, generazione token", dto.getEmail());
            
            String token = jwtUtil.generateToken((UserDetails) auth.getPrincipal());
            
            logger.info("Token generato con successo per utente: {}", dto.getEmail());
            
            return ResponseEntity.ok(Map.of(
                "token", token,
                "email", dto.getEmail()
            ));
        } catch (BadCredentialsException e) {
            logger.warn("Credenziali non valide per email: {}", dto.getEmail());
            return ResponseEntity.status(401).body(Map.of(
                "error", "Credenziali non valide: " + e.getMessage()
            ));
        } catch (UsernameNotFoundException e) {
            logger.warn("Utente non trovato durante login: {}", dto.getEmail());
            return ResponseEntity.status(401).body(Map.of(
                "error", "Utente non trovato: " + dto.getEmail()
            ));
        } catch (Exception e) {
            logger.error("Errore generico durante login: {} - {}", dto.getEmail(), e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Errore di autenticazione: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Endpoint per verificare se una email esiste.
     * @param dto la DTO contenente l'email da verificare
     * @return una risposta che indica se l'email esiste o meno
     */
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody VerifyEmailDto dto) {
        boolean exists = userService.verifyEmail(dto.getEmail());
        return ResponseEntity.ok(Map.of(
            "exists", exists,
            "email", dto.getEmail()
        ));
    }
      /**
     * Endpoint per aggiornare la password di un utente.
     * @param dto la DTO contenente l'email e la nuova password
     * @return una conferma dell'avvenuto aggiornamento
     */
    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordDto dto) {
        try {
            // Aggiungiamo un controllo esplicito per verificare che i parametri non siano nulli o vuoti
            if (dto.getEmail() == null || dto.getEmail().isEmpty() || 
                dto.getNewPassword() == null || dto.getNewPassword().isEmpty()) {
                logger.warn("Tentativo di aggiornamento password con parametri mancanti: email={}", dto.getEmail());
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Email e nuova password sono richiesti"
                ));
            }
            
            Users user = userService.updatePassword(dto.getEmail(), dto.getNewPassword());
            return ResponseEntity.ok(Map.of(
                "message", "Password aggiornata con successo",
                "email", user.getEmail()
            ));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Utente non trovato"
            ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Errore durante l'aggiornamento della password: " + e.getMessage()
            ));
        }
    }
}
