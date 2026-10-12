package org.example.smartbiobackend.config;

import org.example.smartbiobackend.model.*;
import org.example.smartbiobackend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
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

    public InitData(UserRepository userRepository, MovieRepository movieRepository,
                    AuditoriumRepository auditoriumRepository, SeatRepository seatRepository,
                    ShowingRepository showingRepository, RoleRepository roleRepository, BookingRepository bookingRepository,
                    TicketTypeRepository ticketTypeRepository) {
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

    private void dataSetup() {

        Role manager = new Role("Manager");
        Role cleaner = new Role("Cleaner");
        Role admin = new Role("Admin");
        Role serviceDesk = new Role("ServiceDesk");
        Role employee = new Role("Employee");


        roleRepository.save(manager);
        roleRepository.save(cleaner);
        roleRepository.save(admin);
        roleRepository.save(serviceDesk);
        roleRepository.save(employee);
        User managerUser = new User();
        managerUser.setName("Sarah Manager");
        managerUser.setEmail("sarah@smartbio.dk");
        managerUser.setPassword(new BCryptPasswordEncoder().encode("password"));
        managerUser.setRoles(Set.of(manager, employee));

        userRepository.save(managerUser);

        User cleanerUser = new User();
        cleanerUser.setName("John Cleaner");
        cleanerUser.setEmail("john@smartbio.dk");
        cleanerUser.setPassword(new BCryptPasswordEncoder().encode("password"));
        cleanerUser.setRoles(Set.of(cleaner));

        userRepository.save(cleanerUser);

        User adminUser = new User();
        adminUser.setName("Alice Admin");
        adminUser.setEmail("alice@smartbio.dk");
        adminUser.setPassword(new BCryptPasswordEncoder().encode("password"));
        adminUser.setRoles(Set.of(admin));

        userRepository.save(adminUser);

        User serviceDeskUser = new User();
        serviceDeskUser.setName("Bob Service Desk");
        serviceDeskUser.setEmail("bob@smartbio.dk");
        serviceDeskUser.setPassword(new BCryptPasswordEncoder().encode("password"));
        serviceDeskUser.setRoles(Set.of(serviceDesk));

        userRepository.save(serviceDeskUser);

        userRepository.saveAll(List.of(
                createStaffUser("Emily Manager", "emily.manager@smartbio.dk", manager),
                createStaffUser("Lars Manager", "lars.manager@smartbio.dk", manager),
                createStaffUser("Mia Cleaner", "mia.cleaner@smartbio.dk", cleaner),
                createStaffUser("Noah Cleaner", "noah.cleaner@smartbio.dk", cleaner),
                createStaffUser("Oliver Admin", "oliver.admin@smartbio.dk", admin),
                createStaffUser("Freja Admin", "freja.admin@smartbio.dk", admin),
                createStaffUser("Emma Service Desk", "emma.desk@smartbio.dk", serviceDesk),
                createStaffUser("William Service Desk", "william.desk@smartbio.dk", serviceDesk)));

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

        Movie movie = new Movie("Jaws", 124 * 60,
                "Shark movie", "Steven Spielberg", 1975,
                LocalDate.of(1975, 6, 20), 18);
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

        List<Movie> additionalMovies = movieRepository.saveAll(List.of(
                new Movie("The Matrix", 136 * 60, "A computer hacker discovers the world is a simulation.",
                        "Lana and Lilly Wachowski", 1999, LocalDate.of(1999, 3, 31), 15),
                new Movie("Inception", 148 * 60, "A thief enters people's dreams to steal secrets.",
                        "Christopher Nolan", 2010, LocalDate.of(2010, 7, 16), 15),
                new Movie("Interstellar", 169 * 60, "Explorers travel beyond Earth to search for a new home.",
                        "Christopher Nolan", 2014, LocalDate.of(2014, 11, 7), 11),
                new Movie("The Grand Budapest Hotel", 99 * 60, "A hotel concierge and his protégé become involved in a mystery.",
                        "Wes Anderson", 2014, LocalDate.of(2014, 3, 28), 11),
                new Movie("Dune: Part Two", 166 * 60, "A young heir joins a desert people in their struggle for survival.",
                        "Denis Villeneuve", 2024, LocalDate.of(2024, 2, 29), 11),
                new Movie("Paddington 2", 103 * 60, "A polite bear searches for a stolen gift in London.",
                        "Paul King", 2017, LocalDate.of(2017, 11, 10), 7)));

        List<Auditorium> auditoriums = List.of(smallScene, largeScene);
        List<LocalTime> screeningTimes = List.of(LocalTime.of(12, 0), LocalTime.of(18, 0));
        for (int day = 0; day < 7; day++) {
            LocalDate screeningDate = LocalDate.now().plusDays(day + 1L);
            for (int slot = 0; slot < screeningTimes.size(); slot++) {
                for (int auditoriumIndex = 0; auditoriumIndex < auditoriums.size(); auditoriumIndex++) {
                    int movieIndex = (day * 4 + slot * 2 + auditoriumIndex) % additionalMovies.size();
                    Showing scheduledShowing = new Showing(
                            auditoriums.get(auditoriumIndex),
                            additionalMovies.get(movieIndex),
                            screeningDate,
                            screeningDate.atTime(screeningTimes.get(slot)));
                    showingRepository.save(scheduledShowing);
                }
            }
        }

        Booking booking = new Booking(showing, user);
        Seat seat = seatRepository.findBySeatCodeAndAuditorium_Id("1C", 1);
        Seat seat2 = seatRepository.findBySeatCodeAndAuditorium_Id("1B", 1);
        booking.setBookingSeats(
                List.of(new BookingSeat(booking, seat, ticketType), new BookingSeat(booking, seat2, ticketType)));
        booking.setCustomerName(user.getName());
        booking.setCustomerEmail(user.getEmail());
        Seat seat = new Seat(auditorium, "1b");
        seatRepository.save(seat);

        TicketType ticketType = new TicketType("Adult", 95);
        ticketTypeRepository.save(ticketType);

        Booking booking = new Booking(showing, user);
        booking.getBookingSeats().add(new BookingSeat(booking, seat, ticketType));
        bookingRepository.save(booking);

        System.out.println(booking);
    }

    private User createStaffUser(String name, String email, Role role) {
        User staffUser = new User();
        staffUser.setName(name);
        staffUser.setEmail(email);
        staffUser.setPassword("password");
        staffUser.setRoles(Set.of(role));
        return staffUser;
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