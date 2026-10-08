package org.example.smartbiobackend.controller;

import jakarta.validation.Valid;
import org.example.smartbiobackend.model.dto.BookingRequest;
import org.example.smartbiobackend.model.dto.BookingResponse;
import org.example.smartbiobackend.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservation")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/reserve")
    public ResponseEntity<BookingResponse> reserveSeat(@Valid @RequestBody BookingRequest request)  {
        BookingResponse response = bookingService.processBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
