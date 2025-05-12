package com.parcheggio.parcheggio_backend.repository;

import com.parcheggio.parcheggio_backend.model.ParkingSpot;
import com.parcheggio.parcheggio_backend.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByParkingSpot(ParkingSpot parkingSpot);
    Optional<Reservation> findByTicketId(String ticketId);
    List<Reservation> findByEmail(String email);
    List<Reservation> findByIsBusReservation(boolean isBusReservation);
}