package org.example.smartbiobackend.service;

import org.example.smartbiobackend.model.*;
import org.example.smartbiobackend.model.dto.BookingItemRequest;
import org.example.smartbiobackend.model.dto.BookingRequest;
import org.example.smartbiobackend.model.dto.BookingResponse;
import org.example.smartbiobackend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final ShowingRepository showingRepository;
    private final TicketTypeRepository ticketTypeRepository;

    public BookingService(BookingRepository bookingRepository,
                          UserRepository userRepository,
                          SeatRepository seatRepository, ShowingRepository showingRepository, TicketTypeRepository ticketTypeRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.seatRepository = seatRepository;
        this.showingRepository = showingRepository;
        this.ticketTypeRepository = ticketTypeRepository;
    }

    @Transactional
    public BookingResponse processBooking(BookingRequest request) {
        // 1. Fetch referenced entities
        Showing showing = showingRepository.findById(request.showingId()).orElseThrow(()
                -> new IllegalArgumentException("Showing not found"));
        List<String> seatCodesFromRequest = request.seats().stream()
                .map(BookingItemRequest::seatCode).toList();

        Set<Integer> ticketTypeIds = request.seats().stream()
                .map(BookingItemRequest::ticketTypeId)
                .collect(Collectors.toSet());

        List<TicketType> ticketTypes = ticketTypeRepository.findAllById(ticketTypeIds);

        Map<Integer, TicketType> ticketTypeById = ticketTypes.stream()
                .collect(Collectors.toMap(TicketType::getId, Function.identity()));

        List<Seat> seats = seatRepository.findAllByAuditorium_IdAndSeatCodeIn
                (showing.getAuditorium().getId(), seatCodesFromRequest);

        List<BookingItemRequest> items = request.seats();

        Map<String, BookingItemRequest> itemBySeatCode = items.stream()
                .collect(Collectors.toMap(BookingItemRequest::seatCode, Function.identity()));

        Booking booking = new Booking();
        booking.setShowing(showing);

        List<BookingSeat> bookedSeats = seats.stream()
                .map(seat -> {
                    BookingItemRequest item = itemBySeatCode.get(seat.getSeatCode());
                    BookingSeat bookingSeat = new BookingSeat();
                    bookingSeat.setBooking(booking);
                    TicketType type = ticketTypeById.get(item.ticketTypeId());
                    bookingSeat.setSeat(seat);
                    bookingSeat.setTicketType(type);
                    bookingSeat.setTotalPrice(type.getPrice());
                    return bookingSeat;
                }).toList();
        booking.setBookingSeats(bookedSeats);


        if (ticketTypeById.size() != ticketTypeById.size()) {
            // If there is not the same amount of seats then theres a mismatch in the DB
            throw new IllegalArgumentException("Unknown ticket type in request");
        }

        // 2. Initialize email variable here. This is so that we can reassign it with the guest email
        // If the user is registered we instead just grab their info from the repo and set that user on the booking.
        String recipientEmail = "";

        if (request.userId() != null) {
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.userId()));
            booking.setUser(user);
            booking.setCustomerName(user.getName());
            booking.setCustomerEmail(user.getEmail());
            recipientEmail = user.getEmail();
        } else {
            // Guest booking: do not attach a User entity
            if (request.guestMail() == null || request.guestMail().isBlank()) {
                throw new IllegalArgumentException("Guest email is required for unregistered bookings.");
            }
            booking.setCustomerName(request.guestName());
            booking.setCustomerEmail(request.guestMail());
            recipientEmail = request.guestMail();
        }

        // 3. Save booking to DB (guest details are NOT stored)
        Booking savedBooking = bookingRepository.save(booking);

        // 4. Construct JSON response
        return new BookingResponse(
                savedBooking.getId(),
                request.seats(),
                recipientEmail,
                LocalDateTime.now() // Booking time
        );
    }
}
