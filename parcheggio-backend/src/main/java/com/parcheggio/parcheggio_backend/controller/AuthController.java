package com.parcheggio.parcheggio_backend.controller;

import com.parcheggio.parcheggio_backend.dto.LoginDto;
import com.parcheggio.parcheggio_backend.dto.RegisterDto;
import com.parcheggio.parcheggio_backend.model.Users;
import com.parcheggio.parcheggio_backend.service.UsersService;
import com.parcheggio.parcheggio_backend.util.JwtTokenUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UsersService userService;
    private final JwtTokenUtil jwtUtil;

    public AuthController(AuthenticationManager authManager,
                          UsersService userService,
                          JwtTokenUtil jwtUtil) {
        this.authManager = authManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto dto) {
        Users u = userService.register(dto);
        return ResponseEntity.ok("Utente registrato: " + u.getEmail());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto) {
        try {
            Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
            String token = jwtUtil.generateToken((UserDetails) auth.getPrincipal());
            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Credenziali non valide", e);
    }
    }
}
