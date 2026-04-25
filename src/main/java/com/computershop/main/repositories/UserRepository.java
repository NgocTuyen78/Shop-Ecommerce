package com.computershop.main.repositories;

import com.computershop.main.entities.User;
import com.computershop.main.entities.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    Optional<User> findByUsername(String username);
    

    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail")
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);
    
    List<User> findByRole(Role role);
    
    @Query("SELECT u FROM User u WHERE u.role.roleName = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);
    
    boolean existsByUsername(String username);
    

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.roleName = :roleName")
    long countByRoleName(@Param("roleName") String roleName);
    
    @Query("SELECT u FROM User u ORDER BY u.userId DESC")
    List<User> findRecentUsers(Pageable pageable);
}