package com.parcheggio.parcheggio_backend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;
      private Key getSigningKey() {
        // Converti la stringa del segreto in un array di byte
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        
        // Utilizza Keys.hmacShaKeyFor che valida la lunghezza della chiave
        // Se la chiave non è abbastanza lunga/sicura, verrà generata un'eccezione
        try {
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            // Fallback: genera una chiave sicura per HS256 se la chiave configurata non è adeguata
            logger.warn("La chiave JWT configurata non è abbastanza sicura. Utilizzo di una chiave generata automaticamente.");
            return Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }
    }

    public String generateToken(UserDetails userDetails) {
        logger.debug("Generazione token per utente: {}", userDetails.getUsername());
        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
            .signWith(getSigningKey())
            .compact();
    }    

    public String getUsernameFromToken(String token) {
        try {
            String username = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
            logger.debug("Estratto username dal token: {}", username);
            return username;
        } catch (Exception e) {
            logger.error("Errore nell'estrazione dell'username dal token: {}", e.getMessage());
            throw e;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            
            // Controllo validità del soggetto nel token
            boolean validSubject = claims.getBody().getSubject().equals(userDetails.getUsername());
            if (!validSubject) {
                logger.warn("Il soggetto del token non corrisponde all'utente: {} vs {}", 
                          claims.getBody().getSubject(), userDetails.getUsername());
            }
            
            // Controllo scadenza del token
            boolean notExpired = !claims.getBody().getExpiration().before(new Date());
            if (!notExpired) {
                logger.warn("Token scaduto: {}", claims.getBody().getExpiration());
            }
            
            boolean result = validSubject && notExpired;
            logger.debug("Validazione token per {}: {}", userDetails.getUsername(), result);
            return result;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Errore nella validazione del token: {}", e.getMessage());
            return false;
        }
    }
}