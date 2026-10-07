package org.example.smartbiobackend.model.dto;

import java.util.List;

public record BookingSummaryDTO(
        int bookingId,
        String customerName,
        String customerEmail,
        List<String> seatCodes
) {}