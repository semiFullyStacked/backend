package org.example.smartbiobackend.model.dto;

import java.util.List;

public record EmployeeDTO(int id, String name, String email, List<String> roleNames) {}