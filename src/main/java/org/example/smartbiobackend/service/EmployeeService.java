package org.example.smartbiobackend.service;

import org.example.smartbiobackend.model.Role;
import org.example.smartbiobackend.model.StaffRoles;
import org.example.smartbiobackend.model.User;
import org.example.smartbiobackend.model.dto.AssignRolesRequest;
import org.example.smartbiobackend.model.dto.CreateEmployeeRequest;
import org.example.smartbiobackend.model.dto.EmployeeDTO;
import org.example.smartbiobackend.repository.RoleRepository;
import org.example.smartbiobackend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public EmployeeService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    private boolean isEmployee(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> StaffRoles.NAMES.contains(role.getRoleName()));
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

    public EmployeeDTO createEmployee(CreateEmployeeRequest request) {
        if (!StaffRoles.NAMES.contains(request.roleName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown staff role: " + request.roleName());
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setRoles(Set.of(new Role(request.roleName())));

        userRepository.save(user);

        return toEmployeeDTO(user);
    }

    public void deleteEmployee(int id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No employee with id " + id);
        }
        userRepository.deleteById(id);
    }

    public EmployeeDTO assignRoles(int userId, AssignRolesRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No employee with id " + userId));

        for (String roleName : request.roleNames()) {
            if (!StaffRoles.NAMES.contains(roleName)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown staff role: " + roleName);
            }
        }

        Set<Role> roles = request.roleNames().stream()
                .map(this::findOrCreateRole)
                .collect(Collectors.toSet());

        user.setRoles(roles);
        userRepository.save(user);

        return toEmployeeDTO(user);
    }

    private Role findOrCreateRole(String roleName) {
        List<Role> existing = roleRepository.findByRoleName(roleName);
        if (!existing.isEmpty()) {
            return existing.getFirst();
        }
        return roleRepository.save(new Role(roleName));
    }
}
