package com.parcheggio.parcheggio_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.parcheggio.parcheggio_backend.model.Users;

public interface UsersRepository extends JpaRepository<Users, Long> {
}
