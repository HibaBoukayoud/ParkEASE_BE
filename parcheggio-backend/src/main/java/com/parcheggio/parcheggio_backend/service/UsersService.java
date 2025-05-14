package com.parcheggio.parcheggio_backend.service;

import com.parcheggio.parcheggio_backend.dto.RegisterDto;
import com.parcheggio.parcheggio_backend.model.Users;
import com.parcheggio.parcheggio_backend.repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class UsersService implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private static final int MIN_PASSWORD_LENGTH = 6;

    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        logger.info("UsersService inizializzato");
    }

    /**
     * Valida la password per garantire che rispetti i criteri minimi.
     * @param password La password da validare
     * @throws BadCredentialsException se la password non rispetta i criteri
     */
    private void validatePassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            logger.warn("Tentativo di utilizzare una password che non rispetta i criteri minimi (lunghezza < {})", MIN_PASSWORD_LENGTH);
            throw new BadCredentialsException("La password deve essere di almeno " + MIN_PASSWORD_LENGTH + " caratteri");
        }
    }

    public Users register(RegisterDto dto) {
        logger.info("Tentativo di registrazione per email: {}", dto.getEmail());
        
        // Verifica se l'email è già in uso
        if (usersRepository.findByEmail(dto.getEmail()).isPresent()) {
            logger.warn("Tentativo di registrazione con email già in uso: {}", dto.getEmail());
            throw new BadCredentialsException("Email già in uso");
        }
        
        // Valida la password
        validatePassword(dto.getPassword());
        
        Users u = new Users();
        u.setEmail(dto.getEmail());
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        
        Users savedUser = usersRepository.save(u);
        logger.info("Utente registrato con successo: {}", savedUser.getEmail());
        return savedUser;
    }    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Caricamento utente: {}", username);
        
        if (username == null || username.trim().isEmpty()) {
            logger.warn("Tentativo di caricare utente con username nullo o vuoto");
            throw new UsernameNotFoundException("Username non può essere vuoto");
        }
        
        Users user = usersRepository.findByEmail(username)
            .orElseThrow(() -> {
                logger.warn("Utente non trovato: {}", username);
                return new UsernameNotFoundException("Utente non trovato: " + username);
            });
        
        logger.info("Utente trovato: {}", user.getEmail());
        
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            Collections.emptyList() // Nessun ruolo per ora
        );
    }
    
    /**
     * Verifica se un'email esiste nel database.
     * @param email l'email da verificare
     * @return true se l'email esiste, false altrimenti
     */
    public boolean verifyEmail(String email) {
        logger.info("Verifica esistenza email: {}", email);
        boolean exists = usersRepository.findByEmail(email).isPresent();
        logger.info("Email {} {}", email, exists ? "esiste" : "non esiste");
        return exists;
    }
      /**
     * Aggiorna la password di un utente.
     * @param email l'email dell'utente
     * @param newPassword la nuova password
     * @return l'utente aggiornato
     * @throws UsernameNotFoundException se l'utente non esiste
     * @throws BadCredentialsException se la password non rispetta i criteri
     */
    public Users updatePassword(String email, String newPassword) {
        logger.info("Tentativo di aggiornamento password per email: {}", email);
        
        // Verifica input
        if (email == null || email.trim().isEmpty()) {
            logger.warn("Tentativo di aggiornamento password con email nulla o vuota");
            throw new BadCredentialsException("Email non può essere vuota");
        }
        
        Users user = usersRepository.findByEmail(email)
            .orElseThrow(() -> {
                logger.warn("Tentativo di aggiornamento password per utente non trovato: {}", email);
                return new UsernameNotFoundException("Utente non trovato: " + email);
            });
        
        // Valida la nuova password
        validatePassword(newPassword);
        
        user.setPassword(passwordEncoder.encode(newPassword));
        
        Users savedUser = usersRepository.save(user);
        logger.info("Password aggiornata con successo per: {}", savedUser.getEmail());
        return savedUser;
    }
}
