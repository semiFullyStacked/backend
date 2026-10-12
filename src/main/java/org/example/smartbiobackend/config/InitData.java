package org.example.smartbiobackend.config;

import org.example.smartbiobackend.model.*;
import org.example.smartbiobackend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;


@Component
public class InitData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final AuditoriumRepository auditoriumRepository;
    private final SeatRepository seatRepository;
    private final ShowingRepository showingRepository;
    private final RoleRepository roleRepository;
    private final BookingRepository bookingRepository;
    private final TicketTypeRepository ticketTypeRepository;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    private static final List<LocalTime> TIME_SLOTS = List.of(LocalTime.of(15, 0), LocalTime.of(20, 30));
    private static final int WEEKS_TO_SEED = 13; // roughly 3 months

    public InitData(UserRepository userRepository, MovieRepository movieRepository,
                    AuditoriumRepository auditoriumRepository, SeatRepository seatRepository,
                    ShowingRepository showingRepository, RoleRepository roleRepository,
                    BookingRepository bookingRepository, TicketTypeRepository ticketTypeRepository) {
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
    public void run(String... args) {
        if (movieRepository.count() > 0) return; // don't reseed a persistent DB

        seedRolesAndUsers();
        List<Auditorium> auditoriums = seedAuditoriumsAndSeats();
        List<TicketType> ticketTypes = seedTicketTypes();
        List<Movie> movies = seedMovies();
        List<Showing> showings = seedShowings(movies, auditoriums);
        seedSampleBooking(showings.get(0), ticketTypes.get(1));
    }

    private void seedRolesAndUsers() {
        Role manager = roleRepository.save(new Role("Manager"));
        Role cleaner = roleRepository.save(new Role("Cleaner"));
        Role admin = roleRepository.save(new Role("Admin"));
        Role serviceDesk = roleRepository.save(new Role("ServiceDesk"));
        Role employee = roleRepository.save(new Role("Employee"));

        userRepository.saveAll(List.of(
                createStaffUser("Sarah Manager", "sarah@smartbio.dk", manager, employee),
                createStaffUser("John Cleaner", "john@smartbio.dk", cleaner),
                createStaffUser("Alice Admin", "alice@smartbio.dk", admin),
                createStaffUser("Bob Service Desk", "bob@smartbio.dk", serviceDesk),
                createStaffUser("Emily Manager", "emily.manager@smartbio.dk", manager),
                createStaffUser("Lars Manager", "lars.manager@smartbio.dk", manager),
                createStaffUser("Mia Cleaner", "mia.cleaner@smartbio.dk", cleaner),
                createStaffUser("Noah Cleaner", "noah.cleaner@smartbio.dk", cleaner),
                createStaffUser("Oliver Admin", "oliver.admin@smartbio.dk", admin),
                createStaffUser("Freja Admin", "freja.admin@smartbio.dk", admin),
                createStaffUser("Emma Service Desk", "emma.desk@smartbio.dk", serviceDesk),
                createStaffUser("William Service Desk", "william.desk@smartbio.dk", serviceDesk)));

        userRepository.save(new User("David", "mail@mail.dk", LocalDate.now()));
    }

    private List<Auditorium> seedAuditoriumsAndSeats() {
        Auditorium small = auditoriumRepository.save(new Auditorium("Small Scene"));
        Auditorium large = auditoriumRepository.save(new Auditorium("Big Scene"));

        seatRepository.saveAll(createSeats(small, 20, 12)); // 1A to 20L, 240 seats
        seatRepository.saveAll(createSeats(large, 25, 16)); // 1A to 25P, 400 seats

        return List.of(small, large);
    }

    private List<TicketType> seedTicketTypes() {
        return ticketTypeRepository.saveAll(List.of(
                new TicketType("Childrens ticket", 400),
                new TicketType("Adult ticket", 800)));
    }

    private List<Movie> seedMovies() {
        return movieRepository.saveAll(List.of(
                new Movie("Jaws", 124 * 60, "Shark movie", "Steven Spielberg", 1975, LocalDate.of(1975, 6, 20), 18),
                new Movie("Inception", 148 * 60, "A thief who steals corporate secrets through dream-sharing technology.",
                        "Christopher Nolan", 2010, LocalDate.of(2010, 7, 16), 13),
                new Movie("The Lion King", 118 * 60, "A young lion prince flees his kingdom after his father's death.",
                        "Roger Allers", 1994, LocalDate.of(1994, 6, 24), 0),
                new Movie("Parasite", 132 * 60, "Greed and class discrimination threaten the newly formed relationship between two families.",
                        "Bong Joon-ho", 2019, LocalDate.of(2019, 5, 30), 15),
                new Movie("Dune", 155 * 60, "A noble family becomes embroiled in a war for control over the galaxy's most valuable asset.",
                        "Denis Villeneuve", 2021, LocalDate.of(2021, 10, 22), 13),
                new Movie("The Matrix", 136 * 60, "A computer hacker learns the truth about his reality and his role in the war against its controllers.",
                        "Lana and Lilly Wachowski", 1999, LocalDate.of(1999, 3, 31), 15),
                new Movie("Interstellar", 169 * 60, "Explorers travel beyond Earth to search for a new home.",
                        "Christopher Nolan", 2014, LocalDate.of(2014, 11, 7), 11),
                new Movie("The Grand Budapest Hotel", 99 * 60, "A hotel concierge and his protégé become involved in a mystery.",
                        "Wes Anderson", 2014, LocalDate.of(2014, 3, 28), 11),
                new Movie("Dune: Part Two", 166 * 60, "A young heir joins a desert people in their struggle for survival.",
                        "Denis Villeneuve", 2024, LocalDate.of(2024, 2, 29), 11),
                new Movie("Paddington 2", 103 * 60, "A polite bear searches for a stolen gift in London.",
                        "Paul King", 2017, LocalDate.of(2017, 11, 10), 7)));
    }

    private List<Showing> seedShowings(List<Movie> movies, List<Auditorium> auditoriums) {
        LocalDate startDate = LocalDate.now().plusDays(1); // start tomorrow, not today
        List<Showing> showings = new ArrayList<>();

        for (int week = 0; week < WEEKS_TO_SEED; week++) {
            for (int m = 0; m < movies.size(); m++) {
                for (int i = 0; i < 2; i++) { // two showings per movie per week
                    // spread movies across the week; slot is tied to i so two showings
                    // landing on the same day never share a time (no hall double-booking)
                    LocalDate date = startDate.plusWeeks(week).plusDays((m + i * 3L) % 7);
                    LocalTime time = TIME_SLOTS.get(i);
                    Auditorium auditorium = auditoriums.get(m % auditoriums.size());

                    showings.add(new Showing(movies.get(m), auditorium, LocalDateTime.of(date, time)));
                }
            }
        }
        return showingRepository.saveAll(showings);
    }

    private void seedSampleBooking(Showing showing, TicketType ticketType) {
        User user = userRepository.findAll().stream()
                .filter(u -> "David".equals(u.getName())).findFirst().orElseThrow();

        Auditorium auditorium = showing.getAuditorium();
        Seat seat1 = seatRepository.findBySeatCodeAndAuditorium_Id("1B", auditorium.getId());
        Seat seat2 = seatRepository.findBySeatCodeAndAuditorium_Id("1C", auditorium.getId());

        Booking booking = new Booking(showing, user);
        booking.setBookingSeats(List.of(
                new BookingSeat(booking, seat1, ticketType),
                new BookingSeat(booking, seat2, ticketType)));
        booking.setCustomerName(user.getName());
        booking.setCustomerEmail(user.getEmail());
        bookingRepository.save(booking);
    }

    private User createStaffUser(String name, String email, Role... roles) {
        User staffUser = new User();
        staffUser.setName(name);
        staffUser.setEmail(email);
        staffUser.setPassword(encoder.encode("password"));
        staffUser.setRoles(Set.of(roles));
        return staffUser;
    }

    private List<Seat> createSeats(Auditorium auditorium, int rows, int seatsPerRow) {
        return IntStream.rangeClosed(1, rows).boxed()
                .flatMap(row -> IntStream.range(0, seatsPerRow).mapToObj(i -> {
                    Seat seat = new Seat();
                    seat.setAuditorium(auditorium);
                    seat.setSeatCode(row + String.valueOf((char) ('A' + i))); // "1A", "20L", "25P"
                    return seat;
                }))
                .toList();
    }
}