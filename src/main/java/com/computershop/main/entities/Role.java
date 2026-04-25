package com.computershop.main.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
    
    @Id
    @Column(name = "role_id")
    private Integer roleId;
    
    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private String roleName;
    
    public Role() {}
    
    public Role(Integer roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }
    
    public Integer getRoleId() {return roleId;}
    public String getRoleName() {return roleName;}

    public void setRoleId(Integer roleId) {this.roleId = roleId;}
    public void setRoleName(String roleName) {this.roleName = roleName;}

    @Override
    public String toString() {
        return "Role{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}