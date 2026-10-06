package org.example.smartbiobackend.service;

import org.example.smartbiobackend.model.Auditorium;
import org.example.smartbiobackend.model.dto.RoomStatusDTO;
import org.example.smartbiobackend.repository.AuditoriumRepository;
import org.example.smartbiobackend.repository.SeatRepository;
import org.example.smartbiobackend.repository.ShowingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.example.smartbiobackend.model.Showing;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoomStatusService {

    private final AuditoriumRepository auditoriumRepository;
    private final ShowingRepository showingRepository;
    private final SeatRepository seatRepository;

    public RoomStatusService(AuditoriumRepository auditoriumRepository,
                             ShowingRepository showingRepository,
                             SeatRepository seatRepository) {
        this.auditoriumRepository = auditoriumRepository;
        this.showingRepository = showingRepository;
        this.seatRepository = seatRepository;
    }

    public RoomStatusDTO getRoomStatus(int auditoriumId) {
        Auditorium auditorium = auditoriumRepository.findById(auditoriumId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No auditorium with id " + auditoriumId));

        // Get all the showings happened in this auditorium.
        List<Showing> showings = showingRepository.findByAuditorium_Id(auditoriumId);
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime mostRecentEndTime = null;
        String currentMovieTitle = null;
        LocalDateTime currentShowingEndsAt = null;

        //Try to find the movie that ended last
        for (Showing showing : showings) {
            LocalDateTime endTime = showing.getStartTime().plusSeconds(showing.getMovie().getRunTime());

            if (endTime.isBefore(now) && (mostRecentEndTime == null || endTime.isAfter(mostRecentEndTime))) {
                mostRecentEndTime = endTime;
            }

            boolean isCurrentlyPlaying = !showing.getStartTime().isAfter(now) && endTime.isAfter(now);
            if (isCurrentlyPlaying) {
                currentMovieTitle = showing.getMovie().getName();
                currentShowingEndsAt = endTime;
            }
        }

        boolean needsCleaning = mostRecentEndTime != null
                && (auditorium.getLastCleanedAt() == null || auditorium.getLastCleanedAt().isBefore(mostRecentEndTime));

        return new RoomStatusDTO(
                auditoriumId,
                auditorium.getAuditoriumName(),
                needsCleaning,
                currentMovieTitle,
                currentShowingEndsAt,
                auditorium.getLastCleanedAt(),
                seatRepository.countByAuditorium_Id(auditoriumId)
        );
    }

    public RoomStatusDTO markAsCleaned(int auditoriumId) {
        Auditorium auditorium = auditoriumRepository.findById(auditoriumId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No auditorium with id " + auditoriumId));

        auditorium.setLastCleanedAt(LocalDateTime.now());
        auditoriumRepository.save(auditorium);

        return getRoomStatus(auditoriumId);
    }
}