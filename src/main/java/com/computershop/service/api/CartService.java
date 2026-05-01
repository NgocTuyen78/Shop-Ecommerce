package com.computershop.service.api;

import com.computershop.main.entities.Cart;
import com.computershop.main.entities.CartItem;
import com.computershop.main.entities.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Cart operations.
 * Defines the contract for cart-related business logic.
 */
public interface CartService {

    /**
     * Retrieves a cart by user.
     *
     * @param user the user
     * @return optional containing the cart if found
     */
    Optional<Cart> getCartByUser(User user);

    /**
     * Retrieves a cart by user ID.
     *
     * @param userId the user ID
     * @return optional containing the cart if found
     */
    Optional<Cart> getCartByUserId(Integer userId);

    /**
     * Creates a new cart for a user.
     *
     * @param user the user
     * @return the created cart
     */
    Cart createCart(User user);

    /**
     * Adds a product to the cart or updates quantity if already exists.
     *
     * @param userId the user ID
     * @param productId the product ID
     * @param quantity the quantity to add
     * @return the cart item
     */
    CartItem addToCart(Integer userId, Integer productId, Integer quantity);

    /**
     * Updates the quantity of a cart item.
     *
     * @param cartItemId the cart item ID
     * @param quantity the new quantity
     * @return the updated cart item
     */
    CartItem updateCartItemQuantity(Integer cartItemId, Integer quantity);

    /**
     * Removes a cart item from the cart.
     *
     * @param cartItemId the cart item ID
     */
    void removeFromCart(Integer cartItemId);

    /**
     * Clears all items from a cart.
     *
     * @param userId the user ID
     */
    void clearCart(Integer userId);

    /**
     * Retrieves all cart items for a user.
     *
     * @param userId the user ID
     * @return list of cart items
     */
    List<CartItem> getCartItems(Integer userId);

    /**
     * Calculates the total price of items in the cart.
     *
     * @param userId the user ID
     * @return total price
     */
    double getCartTotal(Integer userId);

    /**
     * Gets the total number of items in the cart.
     *
     * @param userId the user ID
     * @return total item count
     */
    int getCartItemCount(Integer userId);

    // Lấy danh sách CartItem theo danh sách ID
    List<CartItem> getCartItemsByIds(List<Integer> ids);

    // Xóa nhiều CartItem theo danh sách ID
    void deleteCartItems(List<Integer> ids);
}
