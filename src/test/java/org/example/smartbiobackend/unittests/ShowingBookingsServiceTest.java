package org.example.smartbiobackend.unittests;

import org.example.smartbiobackend.model.Booking;
import org.example.smartbiobackend.model.BookingSeat;
import org.example.smartbiobackend.model.Seat;
import org.example.smartbiobackend.model.Showing;
import org.example.smartbiobackend.model.TicketType;
import org.example.smartbiobackend.model.User;
import org.example.smartbiobackend.model.dto.BookingSummaryDTO;
import org.example.smartbiobackend.repository.BookingRepository;
import org.example.smartbiobackend.repository.ShowingRepository;
import org.example.smartbiobackend.service.ShowingBookingsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShowingBookingsServiceTest {

    @Mock
    private ShowingRepository showingRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ShowingBookingsService showingBookingsService;

    @Test
    void getBookingsForShowing_WhenShowingHasNoBookings_ReturnsEmptyList() {
        Showing showing = new Showing();
        when(showingRepository.findById(1)).thenReturn(Optional.of(showing));
        when(bookingRepository.findByShowing_IdAndIsPaidTrue(1)).thenReturn(List.of());

        List<BookingSummaryDTO> result = showingBookingsService.getBookingsForShowing(1);

        assertThat(result).isEmpty();
    }

    @Test
    void getBookingsForShowing_WhenShowingHasOnePaidBooking_ReturnsBookingSummary() {
        Showing showing = new Showing();
        Booking booking = new Booking(showing, "Alice", "alice@example.com");

        Seat seat = new Seat("A1");
        TicketType ticketType = new TicketType("Adult", 95);
        booking.getBookingSeats().add(new BookingSeat(booking, seat, ticketType));
        when(showingRepository.findById(1)).thenReturn(Optional.of(showing));
        when(bookingRepository.findByShowing_IdAndIsPaidTrue(1)).thenReturn(List.of(booking));

        List<BookingSummaryDTO> result = showingBookingsService.getBookingsForShowing(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).customerName()).isEqualTo("Alice");
        assertThat(result.get(0).customerEmail()).isEqualTo("alice@example.com");
        assertThat(result.get(0).seatCodes()).containsExactly("A1");
    }

    @Test
    void getBookingsForShowing_WhenBookingIsFromRegisteredUser_ReturnsUserDetailsAndSeat() {
        Showing showing = new Showing();
        User user = new User("Bob", "bob@example.com", LocalDate.of(1990, 1, 1));
        Seat seat = new Seat("B2");
        Booking booking = new Booking(showing, user);
        TicketType ticketType = new TicketType("Childrens ticket", 60);

        booking.setBookingSeats(List.of(new BookingSeat(booking, seat, ticketType)));
        when(showingRepository.findById(1)).thenReturn(Optional.of(showing));
        when(bookingRepository.findByShowing_IdAndIsPaidTrue(1)).thenReturn(List.of(booking));

        List<BookingSummaryDTO> result = showingBookingsService.getBookingsForShowing(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).customerName()).isEqualTo("Bob");
        assertThat(result.get(0).customerEmail()).isEqualTo("bob@example.com");
        assertThat(result.get(0).seatCodes()).containsExactly("B2");
    }

    @Test
    void getBookingsForShowing_WhenShowingDoesNotExist_ThrowsNotFound() {
        when(showingRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> showingBookingsService.getBookingsForShowing(99))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getBookingsForShowing_WhenMultipleBookingsExist_ReturnsAllOfThemCorrectly() {
        Showing showing = new Showing();

        Booking guestBooking = new Booking(showing, "Alice", "alice@example.com");
        Seat seatA = new Seat("A1");
        TicketType ticketType = new TicketType("Adult", 95);
        guestBooking.getBookingSeats().add(new BookingSeat(guestBooking, seatA, ticketType));

        User user = new User("Bob", "bob@example.com", LocalDate.of(1990, 1, 1));
        Seat seatB = new Seat("B2");
        Booking userBooking = new Booking(showing, user);
        userBooking.setBookingSeats(List.of(new BookingSeat(userBooking, seatB, ticketType)));

        when(showingRepository.findById(1)).thenReturn(Optional.of(showing));
        when(bookingRepository.findByShowing_IdAndIsPaidTrue(1)).thenReturn(List.of(guestBooking, userBooking));

        List<BookingSummaryDTO> result = showingBookingsService.getBookingsForShowing(1);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).customerName()).isEqualTo("Alice");
        assertThat(result.get(0).seatCodes()).containsExactly("A1");
        assertThat(result.get(1).customerName()).isEqualTo("Bob");
        assertThat(result.get(1).seatCodes()).containsExactly("B2");
    }
}