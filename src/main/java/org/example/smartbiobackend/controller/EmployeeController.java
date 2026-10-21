package org.example.smartbiobackend.controller;

import org.example.smartbiobackend.model.User;
import org.example.smartbiobackend.model.dto.AssignRolesRequest;
import org.example.smartbiobackend.model.dto.CreateEmployeeRequest;
import org.example.smartbiobackend.model.dto.EmployeeDTO;
import org.example.smartbiobackend.repository.UserRepository;
import org.example.smartbiobackend.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final UserRepository userRepository;

    public EmployeeController(EmployeeService employeeService, UserRepository userRepository) {
        this.employeeService = employeeService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<EmployeeDTO> getAllEmployees() {
        return employeeService.getAllEmployees();
    }


    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }

    @GetMapping("/usersort")
    public ResponseEntity<Map<String, Object>> getPageOfScreenings
            (@RequestParam(defaultValue = "name,asc") String[] sort,
             @RequestParam(defaultValue = "0") int page,
             @RequestParam(defaultValue = "10") int size) {


        for (String s : sort) {
            System.out.println(s);
        }
        List<Sort.Order> orders = new ArrayList<>();
        if (sort[0].contains(",")) {
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
            }
        } else {
            orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
        }
        Pageable paging = PageRequest.of(page, size, Sort.by(orders));

        Page<User> userList = userRepository.findAll(paging);

        Map<String, Object> response = new HashMap<>();
        response.put("users", userList);
        response.put("currentUserPage", userList.getNumber());
        response.put("totalItems", userList.getTotalElements());
        response.put("totalPages", userList.getTotalPages());
        if (userList.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(response, HttpStatus.OK);

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