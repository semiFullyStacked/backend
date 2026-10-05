package org.example.smartbiobackend.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.smartbiobackend.model.Booking;
import org.example.smartbiobackend.model.Payment;
import org.example.smartbiobackend.model.Showing;
import org.example.smartbiobackend.model.User;
import org.example.smartbiobackend.model.dto.PaymentRequest;
import org.example.smartbiobackend.model.dto.TicketDTO;
import org.example.smartbiobackend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PaymentService {
    UserRepository userRepository;
    PaymentRepository paymentRepository;
    TicketService ticketService;
    BookingRepository bookingRepository;

    public PaymentService(UserRepository userRepository, BookingRepository bookingRepository, TicketService ticketService) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.ticketService = ticketService;
    }

    @Transactional
    public TicketDTO processPayment(PaymentRequest request) {
        // Unpack DTO
        Booking booking = bookingRepository
                .findById(request.bookingId())
                .orElseThrow(() -> (new EntityNotFoundException("No showing with id: " + request.bookingId())));

        // Handle guests if booking has no user to tie the booking to
        User user = null;
        if (booking.getUser() != null) {
            user = userRepository
                    .findById(booking.getUser().getId())
                    .orElseThrow(() -> (new EntityNotFoundException("No user with user id: " + booking.getUser().getId())));
        }
        float amount = request.amount();
        // Save the payment
        Payment payment = new Payment(amount,user, booking);
        paymentRepository.save(payment);

        // Confirm booking
        booking.setPaid(true);
        bookingRepository.save(booking);

        // Now return the DTO for the front end
        return ticketService.getTicket(booking.getId());
    }


}
