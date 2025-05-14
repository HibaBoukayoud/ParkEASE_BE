package com.parcheggio.parcheggio_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.parcheggio.parcheggio_backend.model.Users;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
}
