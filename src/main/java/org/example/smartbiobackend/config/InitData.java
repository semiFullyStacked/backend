package org.example.smartbiobackend.config;


import org.example.smartbiobackend.model.*;
import org.example.smartbiobackend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        dataSetup();
    }



    private void dataSetup() {
        User user = new User("David", "mail@mail.dk", LocalDate.now());
        userRepository.save(user);

        Auditorium smallScene = new Auditorium("Small Scene");
        Auditorium largeScene = new Auditorium("Big Scene");
        auditoriumRepository.save(smallScene);
        auditoriumRepository.save(largeScene);

        seatRepository.saveAll(createSeats(smallScene, 20, 12)); // 1A to 20L, 240 seats
        seatRepository.saveAll(createSeats(largeScene, 25, 16)); // 1A to 25P, 400 seats

        TicketType ticketType = new TicketType("Childrens ticket", 400);
        TicketType ticketType2 = new TicketType("Adult ticket", 800);

        ticketTypeRepository.save(ticketType);
        ticketTypeRepository.save(ticketType2);

        Movie movie = new Movie("Jaws", 2000,
                "Shark movie"
                , "Steven Spielberg", 1975,
                LocalDate.of(1975,6,20), 18);

        movieRepository.save(movie);

        Showing showing = new Showing();
        showing.setAuditorium(smallScene);
        showing.setMovie(movie);
        showing.setStartTime(LocalDateTime.now());
        showing.setDate(LocalDate.now());
        showingRepository.save(showing);




        Booking booking = new Booking(showing, user);
        Seat seat = seatRepository.findBySeatCodeAndAuditorium_Id("1C", 1);
        Seat seat2 = seatRepository.findBySeatCodeAndAuditorium_Id("1B", 1);
        booking.setBookingSeats(List.of(new BookingSeat(booking, seat,  ticketType), new BookingSeat(booking, seat2, ticketType)));
        booking.setCustomerName(user.getName());
        booking.setCustomerEmail(user.getEmail());
        bookingRepository.save(booking);

        System.out.println(booking);
    }

    private List<Seat> createSeats(Auditorium auditorium, int rows, int seatsPerRow) {
        return IntStream.rangeClosed(1, rows)
                .boxed()
                .flatMap(row -> IntStream.range(0, seatsPerRow)
                        .mapToObj(i -> {
                            Seat seat = new Seat();
                            seat.setAuditorium(auditorium);
                            seat.setSeatCode(row + String.valueOf((char) ('A' + i))); // "1A", "20L", "25P"
                            return seat;
                        }))
                .toList();
    }
}
