package com.parcheggio.parcheggio_backend.model;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity
@Table(name = "parking_spot")
public class ParkingSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String spotNumber;

    private boolean isOccupied;

    private String vehiclePlate;
    
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isBusSpot;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    @JsonBackReference
    private City city;

    // Costruttori
    public ParkingSpot() {}

    public ParkingSpot(String spotNumber, City city) {
        this.spotNumber = spotNumber;
        this.isOccupied = false;
        this.city = city;
        this.isBusSpot = false;
    }

    // Getters e setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpotNumber() {
        return spotNumber;
    }

    public void setSpotNumber(String spotNumber) {
        this.spotNumber = spotNumber;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }

    public boolean isBusSpot() {
        return isBusSpot;
    }
    
    public void setBusSpot(boolean isBusSpot) {
        this.isBusSpot = isBusSpot;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}