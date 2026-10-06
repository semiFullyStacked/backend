package org.example.smartbiobackend.unittests;

import org.example.smartbiobackend.controller.RoomStatusController;
import org.example.smartbiobackend.model.dto.RoomStatusDTO;
import org.example.smartbiobackend.service.RoomStatusService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomStatusController.class)
class RoomStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomStatusService roomStatusService;

    @Test
    void getRoomStatus_WhenAuditoriumExists_ReturnsStatusJson() throws Exception {
        RoomStatusDTO roomStatus = new RoomStatusDTO(
                1, "Hall 1", true, null, null, null, 0
        );
        when(roomStatusService.getRoomStatus(1)).thenReturn(roomStatus);

        mockMvc.perform(get("/api/auditoriums/1/status").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.auditoriumName").value("Hall 1"))
                .andExpect(jsonPath("$.needsCleaning").value(true));
    }

    @Test
    void markAsCleaned_WhenAuditoriumExists_ReturnsUpdatedStatusJson() throws Exception {
        RoomStatusDTO roomStatus = new RoomStatusDTO(
                1, "Hall 1", false, null, null, LocalDateTime.now(), 0
        );
        when(roomStatusService.markAsCleaned(1)).thenReturn(roomStatus);

        mockMvc.perform(post("/api/auditoriums/1/clean"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.needsCleaning").value(false));
    }

    @Test
    void getRoomStatus_WhenAuditoriumDoesNotExist_ReturnsNotFound() throws Exception {
        when(roomStatusService.getRoomStatus(99))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No auditorium with id 99"));

        mockMvc.perform(get("/api/auditoriums/99/status"))
                .andExpect(status().isNotFound());
    }

    @Test
    void markAsCleaned_WhenAuditoriumDoesNotExist_ReturnsNotFound() throws Exception {
        when(roomStatusService.markAsCleaned(99))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No auditorium with id 99"));

        mockMvc.perform(post("/api/auditoriums/99/clean"))
                .andExpect(status().isNotFound());
    }
}