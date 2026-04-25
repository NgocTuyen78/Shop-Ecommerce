package com.computershop.main.repositories;

import com.computershop.main.entities.PasswordResetToken;
import com.computershop.main.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    
    Optional<PasswordResetToken> findByToken(String token);
    
    Optional<PasswordResetToken> findByUser(User user);
    
    void deleteByUser(User user);
}
