package org.example.smartbiobackend.controller;

import org.example.smartbiobackend.model.dto.TicketDTO;
import org.example.smartbiobackend.service.TicketService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/{bookingId}/ticket")
    public TicketDTO getTicket(@PathVariable int bookingId) {
        return ticketService.getTicket(bookingId);
    }

    //@GetMapping("/{bookingId}/qr-code")
    //public String getQRCode(@PathVariable int bookingId) { return ticketService.getTicketQRCode(bookingId); }
}