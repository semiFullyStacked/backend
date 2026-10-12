package org.example.smartbiobackend.unittests;

import org.example.smartbiobackend.model.*;
import org.example.smartbiobackend.model.dto.SeatAvailabilityDTO;
import org.example.smartbiobackend.model.dto.ShowingSeatMapDTO;
import org.example.smartbiobackend.repository.BookingRepository;
import org.example.smartbiobackend.repository.SeatRepository;
import org.example.smartbiobackend.repository.ShowingRepository;
import org.example.smartbiobackend.service.SeatMapService;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeatMapServiceTest {

    @Mock
    private ShowingRepository showingRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private SeatMapService seatMapService;

    @Test
    void getSeatMap_WhenShowingDoesNotExist_ThrowsNotFound() {
        when(showingRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seatMapService.getSeatMap(99))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getSeatMap_WhenNoBookingsExist_ReturnsAllSeatsAvailable() {
        Auditorium auditorium = new Auditorium("Hall 1");
        auditorium.setId(1);
        Movie movie = new Movie("Inception");
        Showing showing = new Showing(movie, auditorium, LocalDateTime.of(2026, 10, 10, 18, 0));

        Seat seatA1 = new Seat(auditorium, "A1");
        Seat seatA2 = new Seat(auditorium, "A2");

        when(showingRepository.findById(1)).thenReturn(Optional.of(showing));
        when(seatRepository.findByAuditorium_Id(1)).thenReturn(List.of(seatA1, seatA2));
        when(bookingRepository.findByShowing_IdAndIsPaidTrue(1)).thenReturn(List.of());

        ShowingSeatMapDTO result = seatMapService.getSeatMap(1);

        assertThat(result.movieTitle()).isEqualTo("Inception");
        assertThat(result.auditoriumName()).isEqualTo("Hall 1");
        assertThat(result.seats()).hasSize(2);
        assertThat(result.seats()).allMatch(seat -> !seat.reserved());
    }

    @Test
    void getSeatMap_WhenSomeSeatsAreReserved_MarksOnlyThoseAsReserved() {
        Auditorium auditorium = new Auditorium("Hall 1");
        auditorium.setId(1);
        Movie movie = new Movie("Inception");
        Showing showing = new Showing(movie, auditorium, LocalDateTime.of(2026, 10, 10, 18, 0));

        Seat seatA1 = new Seat(auditorium, "A1");
        Seat seatA2 = new Seat(auditorium, "A2");

        Booking booking = new Booking(showing, "Alice", "alice@example.com");
        TicketType ticketType = new TicketType("Adult", 95);
        booking.getBookingSeats().add(new BookingSeat(booking, seatA1, ticketType));

        when(showingRepository.findById(1)).thenReturn(Optional.of(showing));
        when(seatRepository.findByAuditorium_Id(1)).thenReturn(List.of(seatA1, seatA2));
        when(bookingRepository.findByShowing_IdAndIsPaidTrue(1)).thenReturn(List.of(booking));

        ShowingSeatMapDTO result = seatMapService.getSeatMap(1);

        SeatAvailabilityDTO a1 = result.seats().stream().filter(s -> s.seatCode().equals("A1")).findFirst().orElseThrow();
        SeatAvailabilityDTO a2 = result.seats().stream().filter(s -> s.seatCode().equals("A2")).findFirst().orElseThrow();

        assertThat(a1.reserved()).isTrue();
        assertThat(a2.reserved()).isFalse();
    }
}