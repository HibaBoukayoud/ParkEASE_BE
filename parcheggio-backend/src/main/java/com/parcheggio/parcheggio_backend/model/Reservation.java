package com.parcheggio.parcheggio_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parking_spot_id")
    private ParkingSpot parkingSpot;    @Column(nullable = false)
    private String vehiclePlate;
    @Column(nullable = true) // Rendiamo email nullable per compatibilità con dati esistenti
    private String email;
    @Column(nullable = false)
    private LocalDateTime startTime;
    @Column(nullable = false)
    private LocalDateTime endTime;
    @Column(nullable = false)
    private double cost;
    @Column(nullable = false, unique = true)
    private String ticketId; // Campo per il ticket
    
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isBusReservation;

    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ParkingSpot getParkingSpot() { return parkingSpot; }
    public void setParkingSpot(ParkingSpot parkingSpot) { this.parkingSpot = parkingSpot; }
    public String getVehiclePlate() { return vehiclePlate; }    public void setVehiclePlate(String vehiclePlate) { this.vehiclePlate = vehiclePlate; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }
    public boolean isBusReservation() { return isBusReservation; }
    public void setBusReservation(boolean isBusReservation) { this.isBusReservation = isBusReservation; }
}