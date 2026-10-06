package org.example.smartbiobackend.model.dto;

import java.time.LocalDateTime;


//Maybe should change this one to either ShowingDTO or AuditoriumDTO havent decided
public record RoomStatusDTO(
        int auditoriumId,
        String auditoriumName,
        boolean needsCleaning,
        String currentMovieTitle,
        LocalDateTime currentShowingEndsAt,
        LocalDateTime lastCleanedAt,
        int seatCount
) {}