package org.example.smartbiobackend.unittests;

import org.example.smartbiobackend.model.*;
import org.example.smartbiobackend.model.dto.PaymentRequest;
import org.example.smartbiobackend.model.dto.TicketDTO;
import org.example.smartbiobackend.repository.BookingRepository;
import org.example.smartbiobackend.repository.PaymentRepository;
import org.example.smartbiobackend.repository.UserRepository;
import org.example.smartbiobackend.service.PaymentService;
import org.example.smartbiobackend.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private TicketService ticketService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    public Booking booking;

    public Auditorium auditorium;

    public Movie movie;

    public Showing showing;

    public User user;
    @BeforeEach
    void setUp() {
            user = new User("Tommy", "Email", LocalDate.now());
            movie = new Movie("Inception");
            auditorium = new Auditorium("Hall 1");
            showing = new Showing(movie, auditorium, LocalDateTime.of(2026, 10, 1, 20, 0));
            booking = new Booking(showing, "Alice", "alice@example.com");

    }



    @Test
    void processPayment_ShouldSavePayment () {
        // Arrange
        when(bookingRepository.findById(0)).thenReturn(Optional.of(booking));
        PaymentRequest request = new PaymentRequest(10, 0);
        // Act
        TicketDTO payment = paymentService.processPayment(request);
        // Assert
        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);

        // ArgumentCaptor to store the result and do assertions against
        verify(paymentRepository).save(captor.capture());
        Payment saved = captor.getValue();
        assertThat(saved).isNotNull();

        assertThat(saved.getAmount()).isEqualTo(10);


    }
}