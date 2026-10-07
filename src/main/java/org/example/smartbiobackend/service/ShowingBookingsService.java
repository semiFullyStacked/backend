package org.example.smartbiobackend.service;

import org.example.smartbiobackend.model.Booking;
import org.example.smartbiobackend.model.dto.BookingSummaryDTO;
import org.example.smartbiobackend.repository.BookingRepository;
import org.example.smartbiobackend.repository.ShowingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ShowingBookingsService {

    private final ShowingRepository showingRepository;
    private final BookingRepository bookingRepository;

    public ShowingBookingsService(ShowingRepository showingRepository, BookingRepository bookingRepository) {
        this.showingRepository = showingRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<BookingSummaryDTO> getBookingsForShowing(int showingId) {
        showingRepository.findById(showingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No showing with id " + showingId));

        List<Booking> bookings = bookingRepository.findByShowing_IdAndIsPaidTrue(showingId);

        return bookings.stream()
                .map(this::toBookingSummary)
                .toList();
    }

    private BookingSummaryDTO toBookingSummary(Booking booking) {
        if (booking.getUser() != null) { // Registered user
            List<String> seatCodes = booking.getSeat() != null
                    ? List.of(booking.getSeat().getSeatCode())
                    : List.of();
            return new BookingSummaryDTO(
                    booking.getId(),
                    booking.getUser().getName(),
                    booking.getUser().getEmail(),
                    seatCodes
            );
        }

        // not registered user
        List<String> seatCodes = booking.getBookingSeats().stream()
                .map(bookingSeat -> bookingSeat.getSeat().getSeatCode())
                .toList();
        return new BookingSummaryDTO(
                booking.getId(),
                booking.getCustomerName(),
                booking.getCustomerEmail(),
                seatCodes
        );
    }
}