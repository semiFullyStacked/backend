package org.example.smartbiobackend.service;

import org.example.smartbiobackend.model.Role;
import org.example.smartbiobackend.model.User;
import org.example.smartbiobackend.model.dto.EmployeeDTO;
import org.example.smartbiobackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class EmployeeService {

    private final UserRepository userRepository;
    private static final Set<String> STAFF_ROLE_NAMES = Set.of("ServiceDesk", "Cleaner", "Manager", "Admin");

    public EmployeeService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private boolean isEmployee(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> STAFF_ROLE_NAMES.contains(role.getRoleName()));
    }

    private EmployeeDTO toEmployeeDTO(User user) {
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getRoleName)
                .toList();

        return new EmployeeDTO(user.getId(), user.getName(), user.getEmail(), roleNames);
    }

    @Transactional
    public List<EmployeeDTO> getAllEmployees(){
        List<User> allUsers = userRepository.findAll();

        return allUsers.stream()
                .filter(this::isEmployee)
                .map(this::toEmployeeDTO)
                .toList();
    }
}
