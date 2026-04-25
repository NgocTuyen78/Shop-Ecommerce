package com.computershop.service.impl;

import com.computershop.main.entities.Role;
import com.computershop.main.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of RoleService.
 * Handles role-related operations.
 */
@Service
public class RoleServiceImpl {

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Gets the admin role.
     *
     * @return the admin role
     */
    public Role getAdminRole() {
        return roleRepository.findById(1).orElse(null);
    }

    /**
     * Gets the customer role.
     *
     * @return the customer role
     */
    public Role getCustomerRole() {
        return roleRepository.findById(2).orElse(null);
    }

    /**
     * Gets a role by ID.
     *
     * @param id the role ID
     * @return optional containing the role if found
     */
    public Optional<Role> getRoleById(Integer id) {
        return roleRepository.findById(id);
    }

    /**
     * Gets a role by name.
     *
     * @param roleName the role name
     * @return optional containing the role if found
     */
    public Optional<Role> getRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }
}
