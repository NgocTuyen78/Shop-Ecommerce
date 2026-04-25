package com.computershop.service.impl;

import com.computershop.main.entities.User;
import com.computershop.main.entities.Role;
import com.computershop.main.repositories.UserRepository;
import com.computershop.service.api.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of UserService.
 * Handles all user-related business logic.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleServiceImpl roleService;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Integer userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return Optional.empty(); // email field removed
    }

    @Override
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username '" + user.getUsername() + "' already exists");
        }

        if (user.getRole() == null) {
            user.setRole(roleService.getCustomerRole());
        }

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Integer userId, User userDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (!user.getUsername().equals(userDetails.getUsername()) &&
            userRepository.existsByUsername(userDetails.getUsername())) {
            throw new RuntimeException("Username '" + userDetails.getUsername() + "' already exists");
        }


        user.setUsername(userDetails.getUsername());

        if (userDetails.getRole() != null) {
            user.setRole(userDetails.getRole());
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return false; // email not used
    }

    @Override
    public long getTotalUsers() {
        return userRepository.count();
    }

    @Override
    public List<User> getRecentUsers(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return userRepository.findRecentUsers(pageable);
    }

    @Override
    public void toggleUserStatus(Integer userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEnabled(!user.isEnabled());
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with id: " + userId);
        }
    }

    public void resetUserPassword(Integer userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setPasswordHash(hashPassword(newPassword));
        userRepository.save(user);
    }

    @Override
    public boolean validateCredentials(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return verifyPassword(password, user.getPasswordHash());
        }
        return false;
    }

    // ==================== Additional Methods from Original Service ====================

    public Optional<User> getUserByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail);
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public List<User> getUsersByRoleName(String roleName) {
        return userRepository.findByRoleName(roleName);
    }

    public User registerCustomer(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(hashPassword(password));
        user.setRole(roleService.getCustomerRole());
        return createUser(user);
    }

    public void changePassword(Integer userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setPasswordHash(hashPassword(newPassword));
        userRepository.save(user);
    }

    public long countUsersByRole(String roleName) {
        return userRepository.countByRoleName(roleName);
    }

    public Optional<User> authenticate(String usernameOrEmail, String password) {
        Optional<User> user = getUserByUsernameOrEmail(usernameOrEmail);
        if (user.isPresent()) {
            User foundUser = user.get();
            if (!foundUser.isEnabled()) {
                throw new RuntimeException("ACCOUNT_LOCKED");
            }
            if (verifyPassword(password, foundUser.getPasswordHash())) {
                return user;
            }
        }
        return Optional.empty();
    }

    public User registerUser(User user) {
        if (user.getRole() == null) {
            user.setRole(roleService.getCustomerRole());
        }
        user.setPasswordHash(hashPassword(user.getPassword()));
        user.setPassword(null);
        return userRepository.save(user);
    }

    private String hashPassword(String password) {
        return password + "_hashed";
    }

    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (hashedPassword == null) {
            return false;
        }
        if (hashedPassword.equals(plainPassword)) {
            return true;
        }
        return (plainPassword + "_hashed").equals(hashedPassword);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }
}
