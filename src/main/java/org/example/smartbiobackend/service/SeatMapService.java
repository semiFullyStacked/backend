package org.example.smartbiobackend.service;

import org.example.smartbiobackend.model.Booking;
import org.example.smartbiobackend.model.Seat;
import org.example.smartbiobackend.model.Showing;
import org.example.smartbiobackend.model.dto.SeatAvailabilityDTO;
import org.example.smartbiobackend.model.dto.ShowingSeatMapDTO;
import org.example.smartbiobackend.repository.BookingRepository;
import org.example.smartbiobackend.repository.SeatRepository;
import org.example.smartbiobackend.repository.ShowingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SeatMapService {

    private final ShowingRepository showingRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;

    public SeatMapService(ShowingRepository showingRepository, SeatRepository seatRepository, BookingRepository bookingRepository) {
        this.showingRepository = showingRepository;
        this.seatRepository = seatRepository;
        this.bookingRepository = bookingRepository;
    }

    public ShowingSeatMapDTO getSeatMap(int showingId) {
        Showing showing = showingRepository.findById(showingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No showing with id " + showingId));

        List<Seat> allSeatsInAuditorium = seatRepository.findByAuditorium_Id(showing.getAuditorium().getId());

        List<Booking> paidBookings = bookingRepository.findByShowing_IdAndIsPaidTrue(showingId);
        Set<String> reservedSeatCodes = paidBookings.stream()
                .flatMap(booking -> booking.getBookingSeats().stream())
                .map(bookingSeat -> bookingSeat.getSeat().getSeatCode())
                .collect(Collectors.toSet());

        List<SeatAvailabilityDTO> seatAvailability = allSeatsInAuditorium.stream()
                .map(seat -> new SeatAvailabilityDTO(seat.getSeatCode(), reservedSeatCodes.contains(seat.getSeatCode())))
                .toList();

        return new ShowingSeatMapDTO(
                showingId,
                showing.getMovie().getName(),
                showing.getAuditorium().getAuditoriumName(),
                showing.getStartTime(),
                seatAvailability
        );
    }
}