package com.parcheggio.parcheggio_backend;

import com.parcheggio.parcheggio_backend.model.ParkingSpot;
import com.parcheggio.parcheggio_backend.repository.ParkingSpotRepository;
import com.parcheggio.parcheggio_backend.controller.ParkingSpotController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParkingSpotController.class)
public class ParkingSpotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParkingSpotRepository repository;

    @Test
    public void testGetAllSpots() throws Exception {
        // Dati di test
        ParkingSpot spot1 = new ParkingSpot();
        spot1.setId(1L);
        spot1.setSpotNumber("A1");
        spot1.setOccupied(false);

        ParkingSpot spot2 = new ParkingSpot();
        spot2.setId(2L);
        spot2.setSpotNumber("A2");
        spot2.setOccupied(true);
        spot2.setVehiclePlate("XYZ123");

        List<ParkingSpot> spots = Arrays.asList(spot1, spot2);

        // Mock del repository
        when(repository.findAll()).thenReturn(spots);

        // Esegui la richiesta GET
        mockMvc.perform(get("/api/parking-spots")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].spotNumber").value("A1"))
                .andExpect(jsonPath("$[1].spotNumber").value("A2"))
                .andExpect(jsonPath("$[1].vehiclePlate").value("XYZ123"));
    }

    @Test
    public void testCreateSpot() throws Exception {
        // Dati di test
        ParkingSpot spot = new ParkingSpot();
        spot.setSpotNumber("A3");
        spot.setOccupied(false);

        ParkingSpot savedSpot = new ParkingSpot();
        savedSpot.setId(3L);
        savedSpot.setSpotNumber("A3");
        savedSpot.setOccupied(false);

        // Mock del repository
        when(repository.save(spot)).thenReturn(savedSpot);

        // Esegui la richiesta POST
        mockMvc.perform(post("/api/parking-spots")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"spotNumber\": \"A3\", \"isOccupied\": false, \"vehiclePlate\": null}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.spotNumber").value("A3"));
    }
}
