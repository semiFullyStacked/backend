package org.example.smartbiobackend.model.dto;

import java.time.LocalDateTime;

public record ShowingSummaryDTO(int showingId, LocalDateTime startTime, String auditoriumName) {}