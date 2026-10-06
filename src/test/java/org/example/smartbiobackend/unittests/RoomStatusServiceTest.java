package org.example.smartbiobackend.unittests;

import org.assertj.core.api.AssertionsForClassTypes;
import org.example.smartbiobackend.model.Auditorium;
import org.example.smartbiobackend.model.Movie;
import org.example.smartbiobackend.model.Showing;
import org.example.smartbiobackend.model.dto.RoomStatusDTO;
import org.example.smartbiobackend.repository.AuditoriumRepository;
import org.example.smartbiobackend.repository.SeatRepository;
import org.example.smartbiobackend.repository.ShowingRepository;
import org.example.smartbiobackend.service.RoomStatusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomStatusServiceTest {

    @Mock
    private AuditoriumRepository auditoriumRepository;

    @Mock
    private ShowingRepository showingRepository;

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private RoomStatusService roomStatusService;

    @Test
    void getRoomStatus_WhenNoShowingsEverHappened_ReturnsDoesNotNeedCleaning() {
        Auditorium auditorium = new Auditorium("Hall 1");
        when(auditoriumRepository.findById(1)).thenReturn(Optional.of(auditorium));
        when(showingRepository.findByAuditorium_Id(1)).thenReturn(List.of());
        when(seatRepository.countByAuditorium_Id(1)).thenReturn(0);

        RoomStatusDTO status = roomStatusService.getRoomStatus(1);

        assertThat(status.needsCleaning()).isFalse();
        assertThat(status.currentMovieTitle()).isNull();
        assertThat(status.lastCleanedAt()).isNull();
    }

    @Test
    void getRoomStatus_WhenShowingEndedAndNeverCleaned_ReturnsNeedsCleaning() {
        Auditorium auditorium = new Auditorium("Hall 1");
        Movie movie = new Movie("Jaws");
        movie.setRunTime(7200); // 2 hours, in seconds

        Showing showing = new Showing(movie, auditorium, LocalDateTime.now().minusHours(3));

        when(auditoriumRepository.findById(1)).thenReturn(Optional.of(auditorium));
        when(showingRepository.findByAuditorium_Id(1)).thenReturn(List.of(showing));
        when(seatRepository.countByAuditorium_Id(1)).thenReturn(0);

        RoomStatusDTO status = roomStatusService.getRoomStatus(1);

        assertThat(status.needsCleaning()).isTrue();
    }

    @Test
    void getRoomStatus_WhenShowingCurrentlyPlaying_ReturnsCurrentMovieInfo() {
        Auditorium auditorium = new Auditorium("Hall 1");
        Movie movie = new Movie("Jaws");
        movie.setRunTime(7200); // 2 hours

        Showing showing = new Showing(movie, auditorium, LocalDateTime.now().minusHours(1));

        when(auditoriumRepository.findById(1)).thenReturn(Optional.of(auditorium));
        when(showingRepository.findByAuditorium_Id(1)).thenReturn(List.of(showing));
        when(seatRepository.countByAuditorium_Id(1)).thenReturn(0);

        RoomStatusDTO status = roomStatusService.getRoomStatus(1);

        assertThat(status.currentMovieTitle()).isEqualTo("Jaws");
        assertThat(status.needsCleaning()).isFalse();
    }

    @Test
    void getRoomStatus_WhenShowingEndedButAlreadyCleanedAfter_ReturnsDoesNotNeedCleaning() {
        Auditorium auditorium = new Auditorium("Hall 1");
        Movie movie = new Movie("Jaws");
        movie.setRunTime(7200); // 2 hours

        // ended an hour ago
        Showing showing = new Showing(movie, auditorium, LocalDateTime.now().minusHours(3));

        auditorium.setLastCleanedAt(LocalDateTime.now().minusMinutes(30)); // cleaned 30 min ago after it ended

        when(auditoriumRepository.findById(1)).thenReturn(Optional.of(auditorium));
        when(showingRepository.findByAuditorium_Id(1)).thenReturn(List.of(showing));
        when(seatRepository.countByAuditorium_Id(1)).thenReturn(0);

        RoomStatusDTO status = roomStatusService.getRoomStatus(1);

        assertThat(status.needsCleaning()).isFalse();
    }

    @Test
    void getRoomStatus_WhenAuditoriumDoesNotExist_ThrowsNotFound() {
        when(auditoriumRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomStatusService.getRoomStatus(99))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getRoomStatus_WhenMultipleShowingsEnded_UsesMostRecentForCleaningCheck() {
        Auditorium auditorium = new Auditorium("Hall 1");
        Movie movie = new Movie("Jaws");
        movie.setRunTime(7200); // 2 hours

        // ended 4 hours ago
        Showing olderShowing = new Showing(movie, auditorium, LocalDateTime.now().minusHours(6));
        // ended 1 hour ago
        Showing newerShowing = new Showing(movie, auditorium, LocalDateTime.now().minusHours(3));

        // cleaned between the two showings after the older one ended, before the newer one ended
        auditorium.setLastCleanedAt(LocalDateTime.now().minusHours(5));

        when(auditoriumRepository.findById(1)).thenReturn(Optional.of(auditorium));
        when(showingRepository.findByAuditorium_Id(1)).thenReturn(List.of(olderShowing, newerShowing));
        when(seatRepository.countByAuditorium_Id(1)).thenReturn(0);

        RoomStatusDTO status = roomStatusService.getRoomStatus(1);

        assertThat(status.needsCleaning()).isTrue();
    }

    @Test
    void markAsCleaned_WhenAuditoriumExists_SetsLastCleanedAtAndReturnsUpdatedStatus() {
        Auditorium auditorium = new Auditorium("Hall 1");
        when(auditoriumRepository.findById(1)).thenReturn(Optional.of(auditorium));
        when(showingRepository.findByAuditorium_Id(1)).thenReturn(List.of());
        when(seatRepository.countByAuditorium_Id(1)).thenReturn(0);

        RoomStatusDTO status = roomStatusService.markAsCleaned(1);

        AssertionsForClassTypes.assertThat(auditorium.getLastCleanedAt()).isNotNull();
        AssertionsForClassTypes.assertThat(status.needsCleaning()).isFalse();
        verify(auditoriumRepository).save(auditorium);
    }
}