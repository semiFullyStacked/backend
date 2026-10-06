package org.example.smartbiobackend.unittests;

import org.example.smartbiobackend.model.Role;
import org.example.smartbiobackend.model.User;
import org.example.smartbiobackend.model.dto.EmployeeDTO;
import org.example.smartbiobackend.repository.UserRepository;
import org.example.smartbiobackend.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private UserRepository userRepository;

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
}