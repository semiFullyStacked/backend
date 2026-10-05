package org.example.smartbiobackend.model.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TicketDTO(int bookingId, String movieTitle, LocalDateTime showingStart, String auditoriumName,
                        List<TicketSeatDTO> seats, String customerName, String customerEmail) {

    public String getQrCode() {
        return "TICKET-" + bookingId;
    }

    public int getTotalPrice() {
        return seats.stream().mapToInt(TicketSeatDTO::price).sum();
    }

}