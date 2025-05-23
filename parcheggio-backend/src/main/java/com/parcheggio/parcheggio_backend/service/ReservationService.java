package com.parcheggio.parcheggio_backend.service;

import com.parcheggio.parcheggio_backend.model.ParkingSpot;
import com.parcheggio.parcheggio_backend.model.Reservation;
import com.parcheggio.parcheggio_backend.repository.ParkingSpotRepository;
import com.parcheggio.parcheggio_backend.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;    /**
     * Crea una nuova prenotazione.
     */
    public Reservation createReservation(Reservation reservation) {
        // Trova il posto auto
        ParkingSpot spot = parkingSpotRepository.findById(reservation.getParkingSpot().getId())
                .orElseThrow(() -> new RuntimeException("Posto non trovato"));

        LocalDateTime startTime = reservation.getStartTime();
        LocalDateTime endTime = reservation.getEndTime();
        LocalDateTime now = LocalDateTime.now();
        
        // Verifica che la data di inizio sia nel futuro
        if (startTime.isBefore(now)) {
            throw new RuntimeException("Impossibile creare una prenotazione con data di inizio nel passato.");
        }
        
        // Verifica che la data di fine sia successiva alla data di inizio
        if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
            throw new RuntimeException("La data di fine deve essere successiva alla data di inizio.");
        }
        
        // Controlla sovrapposizione di orari
        if (hasOverlappingReservation(spot, startTime, endTime)) {
            throw new RuntimeException("Sovrapposizione di orari: il posto è già prenotato in questo intervallo.");
        }

        // Calcola il costo
        double cost = calculateCost(startTime, endTime, reservation.isBusReservation());
        reservation.setCost(cost);

        // Genera il ticketId univoco
        reservation.setTicketId(UUID.randomUUID().toString());

        // Salva la prenotazione
        Reservation createdReservation = reservationRepository.save(reservation);

        // Aggiorna lo stato del posto
        updateSpotStatus(spot.getId(), reservation.getVehiclePlate());

        return createdReservation;
    }

    /**
     * Restituisce tutte le prenotazioni, senza targa per privacy.
     */
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        // Rimuovi la targa per privacy
        reservations.forEach(reservation -> {
            if (reservation.getParkingSpot() != null) {
                reservation.getParkingSpot().setVehiclePlate(null);
            }
        });
        return reservations;
    }    /**
     * Trova una prenotazione tramite ticketId.
     */
    public Reservation findByTicketId(String ticketId) {
        return reservationRepository.findByTicketId(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket non trovato"));
    }
      /**
     * Trova tutte le prenotazioni per un posto specifico.
     */
    public List<Reservation> findByParkingSpotId(Long spotId) {
        ParkingSpot spot = parkingSpotRepository.findById(spotId)
                .orElseThrow(() -> new RuntimeException("Posto non trovato"));
        return reservationRepository.findByParkingSpot(spot);
    }
      /**
     * Trova tutte le prenotazioni per una email specifica.
     */
    public List<Reservation> findByEmail(String email) {
        return reservationRepository.findByEmail(email);
    }
    
    /**
     * Trova una prenotazione per ID.
     */
    public Reservation findById(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }
      /**
     * Aggiorna una prenotazione esistente.
     */
    public Reservation updateReservation(Reservation reservation) {
        // Trova il posto auto
        ParkingSpot spot = parkingSpotRepository.findById(reservation.getParkingSpot().getId())
                .orElseThrow(() -> new RuntimeException("Posto non trovato"));

        LocalDateTime startTime = reservation.getStartTime();
        LocalDateTime endTime = reservation.getEndTime();
        LocalDateTime now = LocalDateTime.now();
        
        // Verifica che la data di inizio sia nel futuro
        if (startTime.isBefore(now)) {
            throw new RuntimeException("Impossibile aggiornare la prenotazione con data di inizio nel passato.");
        }
        
        // Verifica che la data di fine sia successiva alla data di inizio
        if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
            throw new RuntimeException("La data di fine deve essere successiva alla data di inizio.");
        }
        
        // Controlla sovrapposizione di orari (escludendo la prenotazione corrente)
        if (hasOverlappingReservation(spot, startTime, endTime, reservation.getId())) {
            throw new RuntimeException("Sovrapposizione di orari: il posto è già prenotato in questo intervallo.");
        }

        // Ricalcola il costo
        double cost = calculateCost(startTime, endTime, reservation.isBusReservation());
        reservation.setCost(cost);

        // Salva la prenotazione aggiornata
        Reservation updatedReservation = reservationRepository.save(reservation);

        // Aggiorna lo stato del posto
        updateSpotStatus(spot.getId(), reservation.getVehiclePlate());

        return updatedReservation;
    }
    
    /**
     * Elimina una prenotazione.
     */
    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));
                
        // Elimina la prenotazione
        reservationRepository.delete(reservation);
        
        // Aggiorna lo stato del posto
        updateSpotStatus(reservation.getParkingSpot().getId(), null);
    }

    /**
     * Verifica se c'è una sovrapposizione di orari per un dato posto.
     * Versione semplice senza esclusione della prenotazione corrente.
     */
    private boolean hasOverlappingReservation(ParkingSpot spot, LocalDateTime startTime, LocalDateTime endTime) {
        return hasOverlappingReservation(spot, startTime, endTime, null);
    }
    
    /**
     * Verifica se c'è una sovrapposizione di orari per un dato posto, escludendo la prenotazione corrente.
     */
    private boolean hasOverlappingReservation(ParkingSpot spot, LocalDateTime startTime, LocalDateTime endTime, Long excludeReservationId) {
        List<Reservation> reservations = reservationRepository.findByParkingSpot(spot);
        for (Reservation existing : reservations) {
            // Salta la prenotazione corrente da aggiornare
            if (excludeReservationId != null && existing.getId().equals(excludeReservationId)) {
                continue;
            }
            
            LocalDateTime existingStart = existing.getStartTime();
            LocalDateTime existingEnd = existing.getEndTime();
            // Controlla se gli intervalli si sovrappongono
            if (startTime.isBefore(existingEnd) && endTime.isAfter(existingStart)) {
                return true; // Sovrapposizione trovata
            }
        }
        return false; // Nessuna sovrapposizione
    }    /**
     * Calcola il costo della prenotazione: 
     * - Auto: 2 euro/ora (1 euro/mezz'ora) fino a 8 ore, poi 25 euro/giorno.
     * - Bus: gratis
     */
    private double calculateCost(LocalDateTime startTime, LocalDateTime endTime, boolean isBusReservation) {
        if (isBusReservation) {
            // I bus non pagano
            return 0.0;
        } else {
            // Tariffa per le auto con supporto per mezz'ore
            // Calcola la differenza in minuti e poi converti in mezz'ore
            long totalMinutes = ChronoUnit.MINUTES.between(startTime, endTime);
            long halfHours = (totalMinutes + 29) / 30; // Arrotonda per eccesso alla mezz'ora successiva
            
            if (halfHours <= 16) { // 16 mezz'ore = 8 ore
                return halfHours * 1.0; // 1 euro per mezz'ora
            } else {
                // Per più di 8 ore, calcola i giorni (arrotondando per eccesso)
                long days = (totalMinutes + 1439) / 1440; // 1440 minuti in un giorno
                return days * 25.0; // 25 euro al giorno per le auto
            }
        }
    }

    /**
     * Aggiorna lo stato del posto (isOccupied e vehiclePlate) in base all'orario corrente.
     */
    private void updateSpotStatus(Long spotId, String vehiclePlate) {
        ParkingSpot spot = parkingSpotRepository.findById(spotId)
                .orElseThrow(() -> new RuntimeException("Posto non trovato"));

        LocalDateTime now = LocalDateTime.now();
        List<Reservation> reservations = reservationRepository.findByParkingSpot(spot);

        // Controlla se il posto è attualmente occupato
        boolean isCurrentlyOccupied = reservations.stream()
                .anyMatch(res -> now.isAfter(res.getStartTime()) && now.isBefore(res.getEndTime()));

        spot.setOccupied(isCurrentlyOccupied);
        if (isCurrentlyOccupied) {
            spot.setVehiclePlate(vehiclePlate);
        } else {
            spot.setVehiclePlate(null);
        }

        parkingSpotRepository.save(spot);
    }

    /**
     * Trova tutte le prenotazioni per i bus.
     */
    public List<Reservation> findBusReservations() {
        return reservationRepository.findByIsBusReservation(true);
    }
}