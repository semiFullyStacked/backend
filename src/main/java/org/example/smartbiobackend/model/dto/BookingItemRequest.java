package org.example.smartbiobackend.model.dto;

public record BookingItemRequest(
        String seatCode,
        int ticketTypeId
) {
}
