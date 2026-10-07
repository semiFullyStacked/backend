package org.example.smartbiobackend.model.dto;

public record CreateEmployeeRequest(String name, String email, String password, String roleName) {}