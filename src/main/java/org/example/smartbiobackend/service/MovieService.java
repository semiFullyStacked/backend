package org.example.smartbiobackend.service;

import org.example.smartbiobackend.model.Movie;
import org.example.smartbiobackend.model.Showing;
import org.example.smartbiobackend.model.dto.MovieWithShowingsDTO;
import org.example.smartbiobackend.model.dto.ShowingSummaryDTO;
import org.example.smartbiobackend.repository.ShowingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private final ShowingRepository showingRepository;

    public MovieService(ShowingRepository showingRepository) {
        this.showingRepository = showingRepository;
    }

    public List<MovieWithShowingsDTO> getUpcomingMovies() {
        List<Showing> upcomingShowings = showingRepository.findByStartTimeAfter(LocalDateTime.now());

        Map<Integer, List<Showing>> showingsByMovieId = upcomingShowings.stream()
                .collect(Collectors.groupingBy(showing -> showing.getMovie().getId()));

        return showingsByMovieId.values().stream()
                .map(this::toMovieWithShowingsDTO)
                .toList();
    }

    private MovieWithShowingsDTO toMovieWithShowingsDTO(List<Showing> showingsForOneMovie) {
        Movie movie = showingsForOneMovie.getFirst().getMovie();

        List<ShowingSummaryDTO> showingDTOs = showingsForOneMovie.stream()
                .map(showing -> new ShowingSummaryDTO(
                        showing.getId(),
                        showing.getStartTime(),
                        showing.getAuditorium().getAuditoriumName()
                ))
                .toList();

        return new MovieWithShowingsDTO(movie.getId(), movie.getName(), movie.getAgeRestriction(), showingDTOs);
    }
}