package org.example.smartbiobackend.config;


import org.example.smartbiobackend.model.*;
import org.example.smartbiobackend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
        setupABooking();
        seedMoviesAndShowings();
    }

    private void setupABooking() {
        User user = new User("David", "mail@mail.dk", LocalDate.now());
        userRepository.save(user);

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

        Seat seat = new Seat(auditorium, "1b");
        seatRepository.save(seat);

        TicketType ticketType = new TicketType("Adult", 95);
        ticketTypeRepository.save(ticketType);

        Booking booking = new Booking(showing, user);
        booking.getBookingSeats().add(new BookingSeat(booking, seat, ticketType));
        bookingRepository.save(booking);

        System.out.println(booking);
    }

    private void seedMoviesAndShowings() {
        List<Movie> movies = List.of(
                movieRepository.save(new Movie("Inception", 8880,
                        "A thief who steals corporate secrets through dream-sharing technology.",
                        "Christopher Nolan", 2010, LocalDate.of(2010, 7, 16), 13)),
                movieRepository.save(new Movie("The Lion King", 7080,
                        "A young lion prince flees his kingdom after his father's death.",
                        "Roger Allers", 1994, LocalDate.of(1994, 6, 24), 0)),
                movieRepository.save(new Movie("Parasite", 7920,
                        "Greed and class discrimination threaten the newly formed relationship between two families.",
                        "Bong Joon-ho", 2019, LocalDate.of(2019, 5, 30), 15)),
                movieRepository.save(new Movie("Dune", 9300,
                        "A noble family becomes embroiled in a war for control over the galaxy's most valuable asset.",
                        "Denis Villeneuve", 2021, LocalDate.of(2021, 10, 22), 13)),
                movieRepository.save(new Movie("The Matrix", 8160,
                        "A computer hacker learns the truth about his reality and his role in the war against its controllers.",
                        "Lana Wachowski", 1999, LocalDate.of(1999, 3, 31), 15))
        );

        List<Auditorium> auditoriums = List.of(
                auditoriumRepository.save(new Auditorium("Hall 1")),
                auditoriumRepository.save(new Auditorium("Hall 2")),
                auditoriumRepository.save(new Auditorium("Hall 3"))
        );

        for (Auditorium auditorium : auditoriums) {
            for (char row = 'A'; row <= 'F'; row++) {
                for (int number = 1; number <= 10; number++) {
                    seatRepository.save(new Seat(auditorium, "" + row + number));
                }
            }
        }

        List<LocalTime> timeSlots = List.of(LocalTime.of(15, 0), LocalTime.of(20, 30));

        LocalDate startDate = LocalDate.now().plusDays(1); // start tomorrow, not today
        int weeksToSeed = 13; // roughly 3 months
        int showingCounter = 0;

        for (int week = 0; week < weeksToSeed; week++) {
            for (Movie movie : movies) {
                for (int i = 0; i < 2; i++) { // two showings per movie per week
                    LocalDate showingDate = startDate.plusWeeks(week).plusDays(i * 3L);
                    LocalTime time = timeSlots.get(showingCounter % timeSlots.size());
                    Auditorium auditorium = auditoriums.get(showingCounter % auditoriums.size());

                    Showing showing = new Showing(movie, auditorium, LocalDateTime.of(showingDate, time));
                    showingRepository.save(showing);

                    showingCounter++;
                }
            }
        }

        System.out.println("Seeded " + showingCounter + " showings across " + movies.size() + " movies.");
    }
}