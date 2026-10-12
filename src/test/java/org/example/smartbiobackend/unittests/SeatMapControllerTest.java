package org.example.smartbiobackend.unittests;

import org.example.smartbiobackend.controller.SeatMapController;
import org.example.smartbiobackend.model.dto.SeatAvailabilityDTO;
import org.example.smartbiobackend.model.dto.ShowingSeatMapDTO;
import org.example.smartbiobackend.service.SeatMapService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SeatMapController.class)
class SeatMapControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SeatMapService seatMapService;

    @Test
    void getSeatMap_WhenShowingExists_ReturnsSeatMapJson() throws Exception {
        ShowingSeatMapDTO seatMap = new ShowingSeatMapDTO(
                1, "Inception", "Hall 1", LocalDateTime.of(2026, 10, 10, 18, 0),
                List.of(new SeatAvailabilityDTO("A1", true), new SeatAvailabilityDTO("A2", false))
        );
        when(seatMapService.getSeatMap(1)).thenReturn(seatMap);

        mockMvc.perform(get("/api/showings/1/seatmap").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seats[0].seatCode").value("A1"))
                .andExpect(jsonPath("$.seats[0].reserved").value(true));
    }

    @Test
    void getSeatMap_WhenShowingDoesNotExist_ReturnsNotFound() throws Exception {
        when(seatMapService.getSeatMap(99))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No showing with id 99"));

        mockMvc.perform(get("/api/showings/99/seatmap"))
                .andExpect(status().isNotFound());
    }
}