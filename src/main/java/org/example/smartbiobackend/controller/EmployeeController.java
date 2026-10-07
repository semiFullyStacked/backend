package org.example.smartbiobackend.controller;

import org.example.smartbiobackend.model.dto.AssignRolesRequest;
import org.example.smartbiobackend.model.dto.CreateEmployeeRequest;
import org.example.smartbiobackend.model.dto.EmployeeDTO;
import org.example.smartbiobackend.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeDTO> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @PostMapping
    public EmployeeDTO createEmployee(@RequestBody CreateEmployeeRequest request) {
        return employeeService.createEmployee(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable int id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/roles")
    public EmployeeDTO assignRoles(@PathVariable int id, @RequestBody AssignRolesRequest request) {
        return employeeService.assignRoles(id, request);
    }
}