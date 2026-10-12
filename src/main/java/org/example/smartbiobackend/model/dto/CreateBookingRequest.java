package org.example.smartbiobackend.model.dto;

import java.util.List;

public record CreateBookingRequest(
        int showingId, String customerName, String customerEmail, List<SeatSelectionDTO> seats
) {}