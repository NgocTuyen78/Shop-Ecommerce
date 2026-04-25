package com.computershop.main.repositories;

import com.computershop.main.entities.Cart;
import com.computershop.main.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    
    Optional<Cart> findByUser(User user);
    
    Optional<Cart> findByUserUserId(Integer userId);
    
    @Query("""
            SELECT DISTINCT c
            FROM Cart c
            LEFT JOIN FETCH c.cartItems ci
            LEFT JOIN FETCH ci.product p
            LEFT JOIN FETCH p.image
            WHERE c.user.userId = :userId
            """)
    Optional<Cart> findByUserIdWithItems(@Param("userId") Integer userId);
    
    boolean existsByUserUserId(Integer userId);
    
    void deleteByUserUserId(Integer userId);
}
