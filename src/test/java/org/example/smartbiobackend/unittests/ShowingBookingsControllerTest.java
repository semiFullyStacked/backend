package org.example.smartbiobackend.unittests;

import org.example.smartbiobackend.controller.ShowingBookingsController;
import org.example.smartbiobackend.model.dto.BookingSummaryDTO;
import org.example.smartbiobackend.service.ShowingBookingsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShowingBookingsController.class)
class ShowingBookingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShowingBookingsService showingBookingsService;

    @Test
    void getBookings_WhenShowingExists_ReturnsBookingsJson() throws Exception {
        BookingSummaryDTO booking = new BookingSummaryDTO(1, "Alice", "alice@example.com", List.of("A1"));
        when(showingBookingsService.getBookingsForShowing(1)).thenReturn(List.of(booking));

        mockMvc.perform(get("/api/showings/1/bookings").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("Alice"))
                .andExpect(jsonPath("$[0].seatCodes[0]").value("A1"));
    }

    @Test
    void getBookings_WhenShowingDoesNotExist_ReturnsNotFound() throws Exception {
        when(showingBookingsService.getBookingsForShowing(99))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No showing with id 99"));

        mockMvc.perform(get("/api/showings/99/bookings"))
                .andExpect(status().isNotFound());
    }
}