package com.parcheggio.parcheggio_backend.filter;

import com.parcheggio.parcheggio_backend.util.JwtTokenUtil;
import com.parcheggio.parcheggio_backend.service.UsersService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenUtil jwtUtil;
    private final UsersService userService;

    public JwtAuthenticationFilter(JwtTokenUtil jwtUtil, UsersService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        logger.info("JwtAuthenticationFilter inizializzato");
    }    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {
        try {
            // Controllo se è una richiesta di autenticazione o un OPTIONS (preflight)
            String requestURI = request.getRequestURI();
            String method = request.getMethod();
              // Bypass per endpoint di autenticazione e richieste OPTIONS
            if (requestURI.startsWith("/auth/") || "OPTIONS".equals(method) || 
                requestURI.startsWith("/api/cities") || requestURI.startsWith("/api/parking-spots") ||
                requestURI.startsWith("/api/reservations/by-parking-spot")) {
                logger.info("Bypassando JWT per richiesta {} {}", method, requestURI);
                chain.doFilter(request, response);
                return;
            }
            
            logger.debug("Controllo token JWT per URI: {}", requestURI);
            
            String header = request.getHeader("Authorization");
            String token = null;
            String username = null;
            
            if (header != null && header.startsWith("Bearer ")) {
                token = header.substring(7);
                try {
                    username = jwtUtil.getUsernameFromToken(token);
                    logger.debug("Utente estratto dal token: {}", username);
                } catch (Exception e) {
                    logger.warn("Errore nell'estrazione dell'utente dal token: {}", e.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\": \"Token non valido: " + e.getMessage() + "\"}");
                    response.setContentType("application/json");
                    return;
                }
            } else {
                logger.debug("Nessun token JWT trovato nell'header per: {}", requestURI);
            }
              if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = userService.loadUserByUsername(username);
                    if (jwtUtil.validateToken(token, userDetails)) {
                        logger.debug("Token JWT valido per l'utente: {}", username);
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());
                        auth.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    } else {
                        logger.warn("Token JWT non valido per l'utente: {}", username);
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("{\"error\": \"Token non valido o scaduto\"}");
                        response.setContentType("application/json");
                        return;
                    }
                } catch (UsernameNotFoundException e) {
                    logger.warn("Utente non trovato durante la validazione del token: {}", e.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\": \"Utente non trovato: " + e.getMessage() + "\"}");
                    response.setContentType("application/json");
                    return;
                } catch (Exception e) {
                    logger.warn("Errore durante la validazione del token: {}", e.getMessage());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"error\": \"Errore interno: " + e.getMessage() + "\"}");
                    response.setContentType("application/json");
                    return;
                }
            }
            
            chain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Errore non gestito nel filtro JWT: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Errore interno: " + e.getMessage() + "\"}");
            response.setContentType("application/json");
        }
    }
}
