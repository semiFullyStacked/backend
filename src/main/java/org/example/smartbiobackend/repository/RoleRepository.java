package org.example.smartbiobackend.repository;

import org.example.smartbiobackend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    List<Role> findByRoleName(String roleName);
}