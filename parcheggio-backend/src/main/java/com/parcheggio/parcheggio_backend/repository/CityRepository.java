package com.parcheggio.parcheggio_backend.repository;

import com.parcheggio.parcheggio_backend.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
}
