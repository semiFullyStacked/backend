package org.example.smartbiobackend.unittests;

import org.example.smartbiobackend.model.TicketType;
import org.example.smartbiobackend.model.dto.TicketTypeDTO;
import org.example.smartbiobackend.repository.TicketTypeRepository;
import org.example.smartbiobackend.service.TicketTypeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketTypeServiceTest {

    @Mock
    private TicketTypeRepository ticketTypeRepository;

    @InjectMocks
    private TicketTypeService ticketTypeService;

    @Test
    void getAllTicketTypes_WhenNoneExist_ReturnsEmptyList() {
        when(ticketTypeRepository.findAll()).thenReturn(List.of());

        List<TicketTypeDTO> result = ticketTypeService.getAllTicketTypes();

        assertThat(result).isEmpty();
    }

    @Test
    void getAllTicketTypes_WhenSomeExist_ReturnsMappedDTOs() {
        TicketType adult = new TicketType("Adult", 95);
        TicketType child = new TicketType("Child", 60);

        when(ticketTypeRepository.findAll()).thenReturn(List.of(adult, child));

        List<TicketTypeDTO> result = ticketTypeService.getAllTicketTypes();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(TicketTypeDTO::ticketName).containsExactlyInAnyOrder("Adult", "Child");
    }
}