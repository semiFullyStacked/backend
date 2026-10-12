package org.example.smartbiobackend.unittests;

import org.example.smartbiobackend.model.Auditorium;
import org.example.smartbiobackend.model.Movie;
import org.example.smartbiobackend.model.Showing;
import org.example.smartbiobackend.model.dto.MovieWithShowingsDTO;
import org.example.smartbiobackend.repository.ShowingRepository;
import org.example.smartbiobackend.service.MovieService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private ShowingRepository showingRepository;

    @InjectMocks
    private MovieService movieService;

    @Test
    void getUpcomingMovies_WhenNoShowingsExist_ReturnsEmptyList() {
        when(showingRepository.findByStartTimeAfter(any())).thenReturn(List.of());

        List<MovieWithShowingsDTO> result = movieService.getUpcomingMovies();

        assertThat(result).isEmpty();
    }

    @Test
    void getUpcomingMovies_WhenOneMovieHasTwoShowings_GroupsThemTogether() {
        Movie movie = new Movie("Inception");
        movie.setId(1);
        Auditorium hall1 = new Auditorium("Hall 1");
        Auditorium hall2 = new Auditorium("Hall 2");

        Showing showingA = new Showing(movie, hall1, LocalDateTime.of(2026, 10, 10, 18, 0));
        showingA.setId(10);
        Showing showingB = new Showing(movie, hall2, LocalDateTime.of(2026, 10, 10, 21, 0));
        showingB.setId(11);

        when(showingRepository.findByStartTimeAfter(any())).thenReturn(List.of(showingA, showingB));

        List<MovieWithShowingsDTO> result = movieService.getUpcomingMovies();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).movieId()).isEqualTo(1);
        assertThat(result.get(0).title()).isEqualTo("Inception");
        assertThat(result.get(0).showings()).hasSize(2);
        assertThat(result.get(0).showings().get(0).auditoriumName()).isEqualTo("Hall 1");
        assertThat(result.get(0).showings().get(1).auditoriumName()).isEqualTo("Hall 2");
    }

    @Test
    void getUpcomingMovies_WhenTwoDifferentMoviesExist_ReturnsSeparateEntries() {
        Movie inception = new Movie("Inception");
        inception.setId(1);
        Movie jaws = new Movie("Jaws");
        jaws.setId(2);

        Auditorium hall1 = new Auditorium("Hall 1");

        Showing inceptionShowing = new Showing(inception, hall1, LocalDateTime.of(2026, 10, 10, 18, 0));
        inceptionShowing.setId(10);
        Showing jawsShowing = new Showing(jaws, hall1, LocalDateTime.of(2026, 10, 10, 20, 0));
        jawsShowing.setId(11);

        when(showingRepository.findByStartTimeAfter(any())).thenReturn(List.of(inceptionShowing, jawsShowing));

        List<MovieWithShowingsDTO> result = movieService.getUpcomingMovies();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(MovieWithShowingsDTO::title)
                .containsExactlyInAnyOrder("Inception", "Jaws");

        MovieWithShowingsDTO inceptionResult = result.stream()
                .filter(dto -> dto.title().equals("Inception"))
                .findFirst()
                .orElseThrow();
        assertThat(inceptionResult.showings()).hasSize(1);
    }
}