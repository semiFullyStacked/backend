package org.example.smartbiobackend.unittests;

import org.example.smartbiobackend.model.Role;
import org.example.smartbiobackend.model.User;
import org.example.smartbiobackend.model.dto.AssignRolesRequest;
import org.example.smartbiobackend.model.dto.CreateEmployeeRequest;
import org.example.smartbiobackend.model.dto.EmployeeDTO;
import org.example.smartbiobackend.repository.RoleRepository;
import org.example.smartbiobackend.repository.UserRepository;
import org.example.smartbiobackend.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void getAllEmployees_WhenNoUsersExist_ReturnsEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<EmployeeDTO> result = employeeService.getAllEmployees();

        assertThat(result).isEmpty();
    }

    @Test
    void getAllEmployees_WhenOnlyCustomersExist_ReturnsEmptyList() {
        User customer = new User("Alice", "alice@example.com", LocalDate.of(1995, 1, 1));
        customer.setRoles(Set.of(new Role("Customer")));

        when(userRepository.findAll()).thenReturn(List.of(customer));

        List<EmployeeDTO> result = employeeService.getAllEmployees();

        assertThat(result).isEmpty();
    }

    @Test
    void getAllEmployees_WhenOneEmployeeWithSingleRole_ReturnsEmployeeDTO() {
        User employee = new User(1, "Dana", "dana@example.com", LocalDate.of(1992, 5, 4));
        employee.setRoles(Set.of(new Role("Cleaner")));

        when(userRepository.findAll()).thenReturn(List.of(employee));

        List<EmployeeDTO> result = employeeService.getAllEmployees();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1);
        assertThat(result.get(0).name()).isEqualTo("Dana");
        assertThat(result.get(0).email()).isEqualTo("dana@example.com");
        assertThat(result.get(0).roleNames()).containsExactly("Cleaner");
    }

    @Test
    void getAllEmployees_WhenEmployeeHasMultipleRoles_ReturnsAllRoleNames() {
        User employee = new User(2, "Sam", "sam@example.com", LocalDate.of(1988, 3, 10));
        employee.setRoles(Set.of(new Role("Manager"), new Role("ServiceDesk")));

        when(userRepository.findAll()).thenReturn(List.of(employee));

        List<EmployeeDTO> result = employeeService.getAllEmployees();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).roleNames()).containsExactlyInAnyOrder("Manager", "ServiceDesk");
    }

    @Test
    void getAllEmployees_WhenMixOfCustomersAndEmployees_ReturnsOnlyEmployees() {
        User customer = new User(3, "Alice", "alice@example.com", LocalDate.of(1995, 1, 1));
        customer.setRoles(Set.of(new Role("Customer")));

        User employee = new User(4, "Dana", "dana@example.com", LocalDate.of(1992, 5, 4));
        employee.setRoles(Set.of(new Role("Cleaner")));

        when(userRepository.findAll()).thenReturn(List.of(customer, employee));

        List<EmployeeDTO> result = employeeService.getAllEmployees();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Dana");
    }

    @Test
    void createEmployee_WhenValidRoleGiven_CreatesAndReturnsEmployee() {
        CreateEmployeeRequest request = new CreateEmployeeRequest("Dana", "dana@example.com", "secret123", "Cleaner");

        EmployeeDTO result = employeeService.createEmployee(request);

        assertThat(result.name()).isEqualTo("Dana");
        assertThat(result.email()).isEqualTo("dana@example.com");
        assertThat(result.roleNames()).containsExactly("Cleaner");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createEmployee_WhenRoleIsNotStaffRole_ThrowsBadRequest() {
        CreateEmployeeRequest request = new CreateEmployeeRequest("Alice", "alice@example.com", "secret123", "Customer");

        assertThatThrownBy(() -> employeeService.createEmployee(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Customer");
    }

    @Test
    void deleteEmployee_WhenEmployeeExists_DeletesSuccessfully() {
        when(userRepository.existsById(1)).thenReturn(true);

        employeeService.deleteEmployee(1);

        verify(userRepository).deleteById(1);
    }

    @Test
    void deleteEmployee_WhenEmployeeDoesNotExist_ThrowsNotFound() {
        when(userRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> employeeService.deleteEmployee(99))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("99");
    }

    @Test
    void assignRoles_WhenEmployeeDoesNotExist_ThrowsNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        AssignRolesRequest request = new AssignRolesRequest(List.of("Cleaner"));

        assertThatThrownBy(() -> employeeService.assignRoles(99, request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("99");
    }

    @Test
    void assignRoles_WhenRoleNameIsInvalid_ThrowsBadRequest() {
        User employee = new User(1, "Dana", "dana@example.com", LocalDate.of(1992, 5, 4));
        when(userRepository.findById(1)).thenReturn(Optional.of(employee));

        AssignRolesRequest request = new AssignRolesRequest(List.of("Wizard"));

        assertThatThrownBy(() -> employeeService.assignRoles(1, request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Wizard");
    }

    @Test
    void assignRoles_WhenEmptyListGiven_RemovesAllRoles() {
        User employee = new User(1, "Dana", "dana@example.com", LocalDate.of(1992, 5, 4));
        employee.setRoles(Set.of(new Role("Cleaner")));
        when(userRepository.findById(1)).thenReturn(Optional.of(employee));

        EmployeeDTO result = employeeService.assignRoles(1, new AssignRolesRequest(List.of()));

        assertThat(result.roleNames()).isEmpty();
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void assignRoles_WhenRoleAlreadyExists_ReusesExistingRoleWithoutCreatingDuplicate() {
        User employee = new User(1, "Dana", "dana@example.com", LocalDate.of(1992, 5, 4));
        Role existingCleaner = new Role("Cleaner");

        when(userRepository.findById(1)).thenReturn(Optional.of(employee));
        when(roleRepository.findByRoleName("Cleaner")).thenReturn(List.of(existingCleaner));

        EmployeeDTO result = employeeService.assignRoles(1, new AssignRolesRequest(List.of("Cleaner")));

        assertThat(result.roleNames()).containsExactly("Cleaner");
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void assignRoles_WhenRoleDoesNotExistYet_CreatesAndAssignsNewRole() {
        User employee = new User(1, "Dana", "dana@example.com", LocalDate.of(1992, 5, 4));

        when(userRepository.findById(1)).thenReturn(Optional.of(employee));
        when(roleRepository.findByRoleName("Manager")).thenReturn(List.of());
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeDTO result = employeeService.assignRoles(1, new AssignRolesRequest(List.of("Manager")));

        assertThat(result.roleNames()).containsExactly("Manager");
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void assignRoles_WhenMultipleRolesGiven_AssignsAllOfThem() {
        User employee = new User(1, "Dana", "dana@example.com", LocalDate.of(1992, 5, 4));

        when(userRepository.findById(1)).thenReturn(Optional.of(employee));
        when(roleRepository.findByRoleName("Cleaner")).thenReturn(List.of());
        when(roleRepository.findByRoleName("Manager")).thenReturn(List.of());
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeDTO result = employeeService.assignRoles(1, new AssignRolesRequest(List.of("Cleaner", "Manager")));

        assertThat(result.roleNames()).containsExactlyInAnyOrder("Cleaner", "Manager");
    }
}