package org.example.smartbiobackend.service;

import org.example.smartbiobackend.repository.BookingRepository;
import org.example.smartbiobackend.repository.TicketTypeRepository;
import org.example.smartbiobackend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    UserRepository userRepository;
    BookingRepository bookingRepository;
    TicketTypeRepository ticketTypeRepository;

    public PaymentService(UserRepository userRepository, BookingRepository bookingRepository, TicketTypeRepository ticketTypeRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.ticketTypeRepository = ticketTypeRepository;
    }

    public PaymentService() {
    }




}
