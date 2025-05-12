package com.parcheggio.parcheggio_backend.controller;

import com.parcheggio.parcheggio_backend.model.ParkingSpot;
import com.parcheggio.parcheggio_backend.repository.ParkingSpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/parking-spots")
public class ParkingSpotController {

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @GetMapping
    public List<ParkingSpot> getAllSpots() {
        return parkingSpotRepository.findAll();
    }    @GetMapping("/city/{cityId}")
    public List<ParkingSpot> getParkingSpotsByCity(@PathVariable Long cityId) {
        return parkingSpotRepository.findByCityId(cityId)
                .stream()
                .filter(spot -> !spot.isBusSpot())
                .toList();
    }
    
    // Endpoint rimosso come richiesto, solo per uso interno
    @GetMapping("/city/{cityId}/bus")
    public List<ParkingSpot> getBusParkingSpotsByCity(@PathVariable Long cityId) {
        return parkingSpotRepository.findByCityIdAndIsBusSpot(cityId, true);
    }@PostMapping
    public ParkingSpot createSpot(@RequestBody ParkingSpot spot) {
        return parkingSpotRepository.save(spot);
    }

    @PutMapping("/{id}")
    public ParkingSpot updateSpot(@PathVariable Long id, @RequestBody ParkingSpot updatedSpot) {
        Optional<ParkingSpot> spotOptional = parkingSpotRepository.findById(id);
        if (!spotOptional.isPresent()) {
            throw new IllegalArgumentException("Posto auto con ID " + id + " non trovato");
        }
        ParkingSpot spot = spotOptional.get();
        spot.setOccupied(updatedSpot.isOccupied());
        spot.setVehiclePlate(updatedSpot.getVehiclePlate());
        return parkingSpotRepository.save(spot);
    }
}
