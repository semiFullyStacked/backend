package org.example.smartbiobackend.config;


import org.example.smartbiobackend.model.*;
import org.example.smartbiobackend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class InitData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final AuditoriumRepository auditoriumRepository;
    private final SeatRepository seatRepository;
    private final ShowingRepository showingRepository;
    private final RoleRepository roleRepository;
    private final BookingRepository bookingRepository;
    private final TicketTypeRepository ticketTypeRepository;

    public InitData(UserRepository userRepository, MovieRepository movieRepository, AuditoriumRepository auditoriumRepository, SeatRepository seatRepository, ShowingRepository showingRepository, RoleRepository roleRepository, BookingRepository bookingRepository, TicketTypeRepository ticketTypeRepository) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.auditoriumRepository = auditoriumRepository;
        this.seatRepository = seatRepository;
        this.showingRepository = showingRepository;
        this.roleRepository = roleRepository;
        this.bookingRepository = bookingRepository;
        this.ticketTypeRepository = ticketTypeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Steps:
        // Create x Users
        // Pick a seat in a booking
        // Verify that the seat is reserved by user
        setupABooking();
    }

    private void setupABooking() {
        User user = new User("David", "mail@mail.dk", LocalDate.now());
        userRepository.save(user);

        TicketType ticketType = new TicketType("Childrens ticket", 400);
        TicketType ticketType2 = new TicketType("Adult ticket", 800);

        ticketTypeRepository.save(ticketType);
        ticketTypeRepository.save(ticketType2);

        Movie movie = new Movie("Jaws", 2000,
                "Shark movie"
                , "Steven Spielberg", 1975,
                LocalDate.of(1975,6,20), 18);

        movieRepository.save(movie);
        Auditorium auditorium = new Auditorium("Horror Auditorium");
        auditoriumRepository.save(auditorium);
        Showing showing = new Showing();
        showing.setAuditorium(auditorium);
        showing.setMovie(movie);
        showing.setStartTime(LocalDateTime.now());
        showing.setDate(LocalDate.now());
        showingRepository.save(showing);

        Seat seat = new Seat(auditorium, "1B");
        Seat seat2 = new Seat(auditorium, "2B");

        seatRepository.save(seat);
        seatRepository.save(seat2);
        Booking booking = new Booking(showing, user);
        booking.setBookingSeats(List.of(new BookingSeat(booking, seat, ticketType), new BookingSeat(booking, seat2, ticketType)));
        booking.setCustomerName(user.getName());
        booking.setCustomerEmail(user.getEmail());
        bookingRepository.save(booking);

        System.out.println(booking);
    }
}
