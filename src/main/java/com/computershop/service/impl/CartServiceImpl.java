package com.computershop.service.impl;

import com.computershop.main.entities.Cart;
import com.computershop.main.entities.CartItem;
import com.computershop.main.entities.Product;
import com.computershop.main.entities.User;
import com.computershop.main.repositories.CartRepository;
import com.computershop.main.repositories.CartItemRepository;
import com.computershop.main.repositories.ProductRepository;
import com.computershop.main.repositories.UserRepository;
import com.computershop.service.api.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of CartService.
 * Handles all cart-related business logic.
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<Cart> getCartByUser(User user) {
        return cartRepository.findByUser(user);
    }

    @Override
    public Optional<Cart> getCartByUserId(Integer userId) {
        return cartRepository.findByUserIdWithItems(userId);
    }

    @Override
    public Cart createCart(User user) {
        Cart cart = new Cart(user);
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public CartItem addToCart(Integer userId, Integer productId, Integer quantity) {
        // Ensure cart exists
        Optional<Cart> cartOpt = cartRepository.findByUserIdWithItems(userId);
        Cart cart;

        if (cartOpt.isEmpty()) {
            // Find user and create new cart
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                throw new RuntimeException("User not found: " + userId);
            }
            cart = getOrCreateCart(userOpt.get());
        } else {
            cart = cartOpt.get();
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Not enough stock. Available: " + product.getStockQuantity());
        }

        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getCartId(), productId);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;

            if (product.getStockQuantity() < newQuantity) {
                throw new RuntimeException("Not enough stock. Available: " + product.getStockQuantity());
            }

            item.setQuantity(newQuantity);
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);
            return cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem(cart, product, quantity);
            // Save newItem FIRST so it becomes managed and gets an ID.
            newItem = cartItemRepository.save(newItem);
            // Add managed item to cart to keep OSIV cache synced without triggering double-insert.
            cart.getCartItems().add(newItem);
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);
            return newItem;
        }
    }

    @Override
    @Transactional
    public CartItem updateCartItemQuantity(Integer cartItemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + cartItemId));

        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        if (item.getProduct().getStockQuantity() < quantity) {
            throw new RuntimeException("Not enough stock. Available: " + item.getProduct().getStockQuantity());
        }

        item.setQuantity(quantity);
        item.getCart().setUpdatedAt(LocalDateTime.now());
        cartItemRepository.save(item);
        return item;
    }

    @Override
    @Transactional
    public void removeFromCart(Integer cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + cartItemId));

        Cart cart = item.getCart();
        cart.removeCartItem(item);
        cartItemRepository.delete(item);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart(Integer userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        cart.getCartItems().clear();
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Override
    public List<CartItem> getCartItems(Integer userId) {
        Optional<Cart> cartOpt = cartRepository.findByUserIdWithItems(userId);
        return cartOpt.map(Cart::getCartItems).orElse(List.of());
    }

    @Override
    public double getCartTotal(Integer userId) {
        Optional<Cart> cartOpt = cartRepository.findByUserIdWithItems(userId);

        if (cartOpt.isEmpty()) {
            return 0.0;
        }

        return cartOpt.get().getCartItems().stream()
                .mapToDouble(item -> item.getSubtotal().doubleValue())
                .sum();
    }

    @Override
    public int getCartItemCount(Integer userId) {
        Optional<Cart> cartOpt = cartRepository.findByUserIdWithItems(userId);

        if (cartOpt.isEmpty()) {
            return 0;
        }

        return cartOpt.get().getCartItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    // ==================== Additional Helper Methods ====================

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart(user);
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public void updateCartItemQuantity(Integer userId, Integer productId, Integer quantity) {
        if (quantity == null) {
            throw new RuntimeException("Quantity is required");
        }

        if (quantity <= 0) {
            Cart cart = cartRepository.findByUserIdWithItems(userId)
                    .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));
            cartItemRepository.findByCartIdAndProductId(cart.getCartId(), productId)
                    .ifPresent(item -> removeFromCart(item.getCartItemId()));
            return;
        }

        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Not enough stock. Available: " + product.getStockQuantity());
        }

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getCartId(), productId)
                .orElseThrow(() -> new RuntimeException("Cart item not found for product: " + productId));

        item.setQuantity(quantity);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        cartItemRepository.save(item);
    }

    public List<CartItem> getCartItemsSafe(Integer userId) {
        Optional<Cart> cartOpt = cartRepository.findByUserIdWithItems(userId);
        if (cartOpt.isEmpty() || cartOpt.get().getCartItems() == null) {
            return new ArrayList<>();
        }
        return cartOpt.get().getCartItems();
    }
}
