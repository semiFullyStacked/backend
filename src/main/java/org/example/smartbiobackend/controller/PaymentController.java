package org.example.smartbiobackend.controller;


import org.example.smartbiobackend.model.dto.TicketDTO;
import org.example.smartbiobackend.model.dto.PaymentRequest;
import org.example.smartbiobackend.repository.UserRepository;
import org.example.smartbiobackend.service.PaymentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/payments")
public class PaymentController {

PaymentService paymentService;
UserRepository userRepository;

    public PaymentController(PaymentService paymentService, UserRepository userRepository) {
        this.paymentService = paymentService;
        this.userRepository = userRepository;
    }

    @PostMapping("/tickets/purchase")
    public TicketDTO payForTickets(@RequestBody PaymentRequest request) {
        return paymentService.processPayment(request);
    }
    }

