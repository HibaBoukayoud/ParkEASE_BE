package com.parcheggio.parcheggio_backend.controller;

import com.parcheggio.parcheggio_backend.model.Reservation;
import com.parcheggio.parcheggio_backend.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    /**
     * Crea una nuova prenotazione.
     */    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody Reservation reservation) {
        try {
            Reservation createdReservation = reservationService.createReservation(reservation);
            return ResponseEntity.ok(createdReservation);
        } catch (RuntimeException e) {
            // Restituisce un oggetto con il messaggio di errore
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", e.getMessage())); // Invia il messaggio di errore specifico
        }
    }

    /**
     * Restituisce tutte le prenotazioni, senza targa per privacy.
     */
    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }    /**
     * Valida un ticket per uscire dal parcheggio.
     */
    @GetMapping("/validate-ticket/{ticketId}")
    public ResponseEntity<String> validateTicket(@PathVariable String ticketId) {
        try {
            Reservation reservation = reservationService.findByTicketId(ticketId);
            if (reservation.getEndTime().isAfter(LocalDateTime.now())) {
                return ResponseEntity.ok("Ticket valido");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ticket non valido o scaduto");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket non trovato");
        }
    }
      /**
     * Ottiene le prenotazioni di un posto specifico.
     */    @GetMapping("/by-parking-spot/{spotId}")
    public ResponseEntity<List<Map<String, String>>> getReservationsByParkingSpot(@PathVariable Long spotId) {
        try {
            List<Reservation> reservations = reservationService.findByParkingSpotId(spotId);
            LocalDateTime now = LocalDateTime.now();
            
            // Semplifichiamo i dati da inviare al frontend (solo date e orari)
            // Filtriamo le prenotazioni per mostrare solo quelle future o in corso
            List<Map<String, String>> simplifiedReservations = new ArrayList<>();
            
            for (Reservation r : reservations) {
                // Mostra solo le prenotazioni con la data di fine nel futuro
                if (r.getEndTime().isAfter(now)) {
                    Map<String, String> reservationData = new HashMap<>();
                    reservationData.put("startTime", r.getStartTime().toString());
                    reservationData.put("endTime", r.getEndTime().toString());
                    simplifiedReservations.add(reservationData);
                }
            }
            
            return ResponseEntity.ok(simplifiedReservations);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
      /**
     * Ottiene le prenotazioni per una specifica email.
     */
    @GetMapping("/by-email/{email}")
    public ResponseEntity<List<Reservation>> getReservationsByEmail(@PathVariable String email) {
        try {
            List<Reservation> reservations = reservationService.findByEmail(email);
            return ResponseEntity.ok(reservations);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Ottiene tutte le prenotazioni di bus.
     */
    @GetMapping("/bus")
    public ResponseEntity<List<Reservation>> getBusReservations() {
        List<Reservation> busReservations = reservationService.findBusReservations();
        return ResponseEntity.ok(busReservations);
    }

    /**
     * Aggiorna una prenotazione esistente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(@PathVariable Long id, @RequestBody Reservation reservation) {
        try {
            Reservation existingReservation = reservationService.findById(id);
            if (existingReservation == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Prenotazione non trovata"));
            }
            
            // Aggiorna i campi modificabili
            existingReservation.setVehiclePlate(reservation.getVehiclePlate());
            existingReservation.setEmail(reservation.getEmail());
            existingReservation.setStartTime(reservation.getStartTime());
            existingReservation.setEndTime(reservation.getEndTime());
            
            // Se il posto di parcheggio è cambiato, aggiorna anche quello
            if (reservation.getParkingSpot() != null && 
                !existingReservation.getParkingSpot().getId().equals(reservation.getParkingSpot().getId())) {
                existingReservation.setParkingSpot(reservation.getParkingSpot());
            }
            
            Reservation updatedReservation = reservationService.updateReservation(existingReservation);
            return ResponseEntity.ok(updatedReservation);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Elimina una prenotazione esistente.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long id) {
        try {
            Reservation existingReservation = reservationService.findById(id);
            if (existingReservation == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Prenotazione non trovata"));
            }
            
            reservationService.deleteReservation(id);
            return ResponseEntity.ok(Map.of("message", "Prenotazione eliminata con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
}