package org.example.smartbiobackend.unittests;

import org.example.smartbiobackend.controller.EmployeeController;
import org.example.smartbiobackend.model.dto.EmployeeDTO;
import org.example.smartbiobackend.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    void getAllEmployees_ReturnsEmployeeListJson() throws Exception {
        EmployeeDTO employee = new EmployeeDTO(1, "Dana", "dana@example.com", List.of("Cleaner"));
        when(employeeService.getAllEmployees()).thenReturn(List.of(employee));

        mockMvc.perform(get("/api/employees").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Dana"))
                .andExpect(jsonPath("$[0].roleNames[0]").value("Cleaner"));
    }
}