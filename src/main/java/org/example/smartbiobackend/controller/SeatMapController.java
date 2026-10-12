package org.example.smartbiobackend.controller;

import org.example.smartbiobackend.model.dto.ShowingSeatMapDTO;
import org.example.smartbiobackend.service.SeatMapService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/showings")
public class SeatMapController {

    private final SeatMapService seatMapService;

    public SeatMapController(SeatMapService seatMapService) {
        this.seatMapService = seatMapService;
    }

    @GetMapping("/{showingId}/seatmap")
    public ShowingSeatMapDTO getSeatMap(@PathVariable int showingId) {
        return seatMapService.getSeatMap(showingId);
    }
}