package org.example.smartbiobackend.unittests;

import org.example.smartbiobackend.controller.MovieController;
import org.example.smartbiobackend.model.dto.MovieWithShowingsDTO;
import org.example.smartbiobackend.model.dto.ShowingSummaryDTO;
import org.example.smartbiobackend.service.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    @Test
    void getUpcomingMovies_ReturnsMoviesWithShowingsJson() throws Exception {
        ShowingSummaryDTO showing = new ShowingSummaryDTO(10, LocalDateTime.of(2026, 10, 10, 18, 0), "Hall 1");
        MovieWithShowingsDTO movie = new MovieWithShowingsDTO(1, "Inception", 13, List.of(showing));

        when(movieService.getUpcomingMovies()).thenReturn(List.of(movie));

        mockMvc.perform(get("/api/movies").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Inception"))
                .andExpect(jsonPath("$[0].showings[0].auditoriumName").value("Hall 1"));
    }
}