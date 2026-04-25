package com.computershop.service.api;

import com.computershop.main.entities.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for User operations.
 * Defines the contract for user-related business logic.
 */
public interface UserService {

    /**
     * Retrieves all users.
     *
     * @return list of all users
     */
    List<User> getAllUsers();

    /**
     * Retrieves a user by its ID.
     *
     * @param userId the user ID
     * @return optional containing the user if found
     */
    Optional<User> getUserById(Integer userId);

    /**
     * Retrieves a user by username.
     *
     * @param username the username
     * @return optional containing the user if found
     */
    Optional<User> getUserByUsername(String username);

    /**
     * Retrieves a user by email.
     *
     * @param email the email
     * @return optional containing the user if found
     */
    Optional<User> getUserByEmail(String email);

    /**
     * Creates a new user.
     *
     * @param user the user to create
     * @return the created user
     */
    User createUser(User user);

    /**
     * Updates an existing user.
     *
     * @param userId the user ID
     * @param user the user with updated details
     * @return the updated user
     */
    User updateUser(Integer userId, User user);

    /**
     * Deletes a user by its ID.
     *
     * @param userId the user ID
     */
    void deleteUser(Integer userId);

    /**
     * Checks if a username exists.
     *
     * @param username the username
     * @return true if exists
     */
    boolean existsByUsername(String username);

    /**
     * Checks if an email exists.
     *
     * @param email the email
     * @return true if exists
     */
    boolean existsByEmail(String email);

    /**
     * Retrieves total number of users.
     *
     * @return total user count
     */
    long getTotalUsers();

    /**
     * Retrieves recent users with a limit.
     *
     * @param limit the maximum number of users
     * @return list of recent users
     */
    List<User> getRecentUsers(int limit);

    /**
     * Toggles the active status of a user.
     *
     * @param userId the user ID
     */
    void toggleUserStatus(Integer userId);

    /**
     * Validates user credentials.
     *
     * @param username the username
     * @param password the password
     * @return true if credentials are valid
     */
    boolean validateCredentials(String username, String password);
}
