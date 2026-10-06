package org.example.smartbiobackend.controller;

import org.example.smartbiobackend.model.dto.BookingSummaryDTO;
import org.example.smartbiobackend.service.ShowingBookingsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/showings")
public class ShowingBookingsController {

    private final ShowingBookingsService showingBookingsService;

    public ShowingBookingsController(ShowingBookingsService showingBookingsService) {
        this.showingBookingsService = showingBookingsService;
    }

    @GetMapping("/{showingId}/bookings")
    public List<BookingSummaryDTO> getBookings(@PathVariable int showingId) {
        return showingBookingsService.getBookingsForShowing(showingId);
    }
}