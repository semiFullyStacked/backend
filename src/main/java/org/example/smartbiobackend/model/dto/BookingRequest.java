package org.example.smartbiobackend.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BookingRequest(@NotNull @Size(max = 10)List<BookingItemRequest> seats, Integer userId, String guestName, String guestMail, int showingId) {
}
