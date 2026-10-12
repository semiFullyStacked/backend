package org.example.smartbiobackend.service;

import org.example.smartbiobackend.model.dto.TicketTypeDTO;
import org.example.smartbiobackend.repository.TicketTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketTypeService {

    private final TicketTypeRepository ticketTypeRepository;

    public TicketTypeService(TicketTypeRepository ticketTypeRepository) {
        this.ticketTypeRepository = ticketTypeRepository;
    }

    public List<TicketTypeDTO> getAllTicketTypes() {
        return ticketTypeRepository.findAll().stream()
                .map(t -> new TicketTypeDTO(t.getId(), t.getTicketName(), t.getPrice()))
                .toList();
    }
}