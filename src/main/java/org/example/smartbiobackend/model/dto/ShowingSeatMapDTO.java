package org.example.smartbiobackend.model.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ShowingSeatMapDTO(
        int showingId, String movieTitle, String auditoriumName,
        LocalDateTime startTime, List<SeatAvailabilityDTO> seats
) {}