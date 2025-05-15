package com.parcheggio.parcheggio_backend.config;

import com.parcheggio.parcheggio_backend.filter.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final CorsConfigurationSource corsConfigSource;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter, 
                         CorsConfigurationSource corsConfigurationSource,
                         AuthenticationProvider authenticationProvider) {
        this.jwtFilter = jwtFilter;
        this.corsConfigSource = corsConfigurationSource;
        this.authenticationProvider = authenticationProvider;
    }    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http            .cors(cors -> cors.configurationSource(corsConfigSource))
            .csrf(csrf -> csrf.disable())            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/api/cities/**").permitAll() 
                .requestMatchers("/api/parking-spots/**").permitAll()
                .requestMatchers("/api/reservations/by-parking-spot/**").permitAll()
                .requestMatchers("/api/test/public").permitAll()
                .requestMatchers("/error").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\": \"Non autorizzato: " + authException.getMessage() + "\"}");
                    response.setContentType("application/json");
                })
            );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
