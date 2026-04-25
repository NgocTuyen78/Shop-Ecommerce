package com.computershop.dto;

/**
 * Data Transfer Object for User entity.
 * Used to transfer user data between layers without exposing sensitive information.
 */
public class UserDTO {

    private Integer userId;
    private String username;
    private String email;
    private String roleName;
    private Boolean isActive;

    // ==================== Constructors ====================

    public UserDTO() {
    }

    public UserDTO(Integer userId, String username, String email, String roleName) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.roleName = roleName;
    }

    // ==================== Getters and Setters ====================

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    // ==================== Helper Methods ====================

    /**
     * Checks if the user is an admin.
     *
     * @return true if role is admin
     */
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(roleName);
    }

    /**
     * Checks if the user is a customer.
     *
     * @return true if role is customer
     */
    public boolean isCustomer() {
        return "customer".equalsIgnoreCase(roleName);
    }
}
