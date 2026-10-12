package org.example.smartbiobackend.controller;

import org.example.smartbiobackend.model.dto.MovieWithShowingsDTO;
import org.example.smartbiobackend.service.MovieService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<MovieWithShowingsDTO> getUpcomingMovies() {
        return movieService.getUpcomingMovies();
    }
}