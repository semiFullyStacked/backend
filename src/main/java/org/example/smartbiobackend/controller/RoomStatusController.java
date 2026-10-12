package org.example.smartbiobackend.controller;

import org.example.smartbiobackend.model.dto.RoomStatusDTO;
import org.example.smartbiobackend.service.RoomStatusService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/auditoriums")
public class RoomStatusController {

    private final RoomStatusService roomStatusService;

    public RoomStatusController(RoomStatusService roomStatusService) {
        this.roomStatusService = roomStatusService;
    }

    @GetMapping("/{auditoriumId}/status")
    public RoomStatusDTO getRoomStatus(@PathVariable int auditoriumId) {
        return roomStatusService.getRoomStatus(auditoriumId);
    }

    @PostMapping("/{auditoriumId}/clean")
    public RoomStatusDTO markAsCleaned(@PathVariable int auditoriumId) {
        return roomStatusService.markAsCleaned(auditoriumId);
    }

    @GetMapping("")
    public List<RoomStatusDTO> getAuditoriums() {
        return roomStatusService.getAllRoomStatuses();
    }
}
