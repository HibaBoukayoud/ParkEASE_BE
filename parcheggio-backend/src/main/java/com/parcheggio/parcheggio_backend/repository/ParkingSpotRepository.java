package com.parcheggio.parcheggio_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.parcheggio.parcheggio_backend.model.ParkingSpot;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long>{
    List<ParkingSpot> findByCityId(Long cityId);
    List<ParkingSpot> findByCityIdAndIsBusSpot(Long cityId, boolean isBusSpot);
}

