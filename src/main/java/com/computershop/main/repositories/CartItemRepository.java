package com.computershop.main.repositories;

import com.computershop.main.entities.CartItem;
import com.computershop.main.entities.Cart;
import com.computershop.main.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    
    List<CartItem> findByCart(Cart cart);
    
    List<CartItem> findByCartCartId(Integer cartId);
    
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.cartId = :cartId AND ci.product.productId = :productId")
    Optional<CartItem> findByCartIdAndProductId(@Param("cartId") Integer cartId, @Param("productId") Integer productId);
    
    void deleteByCart(Cart cart);
    
    void deleteByCartCartId(Integer cartId);
    
    long countByCartCartId(Integer cartId);
}
