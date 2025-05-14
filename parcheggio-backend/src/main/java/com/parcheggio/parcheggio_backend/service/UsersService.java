package com.parcheggio.parcheggio_backend.service;

import com.parcheggio.parcheggio_backend.dto.RegisterDto;
import com.parcheggio.parcheggio_backend.model.Users;
import com.parcheggio.parcheggio_backend.repository.UsersRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsersService implements UserDetailsService {
    
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;


    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Users register(RegisterDto dto) {
        Users u = new Users();
        u.setEmail(dto.getEmail());
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        return usersRepository.save(u);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadUserByUsername'");
    }

}
