package com.computershop.dto;

import java.math.BigDecimal;

/**
 * Data Transfer Object for CartItem entity.
 * Used to transfer cart item data between layers.
 */
public class CartItemDTO {

    private Integer cartItemId;
    private Integer cartId;
    private Integer productId;
    private String productName;
    private String productImageUrl;
    private BigDecimal productPrice;
    private Integer quantity;
    private BigDecimal subtotal;

    // ==================== Constructors ====================

    public CartItemDTO() {
    }

    public CartItemDTO(Integer cartItemId, Integer cartId, Integer productId,
                       String productName, BigDecimal productPrice, Integer quantity) {
        this.cartItemId = cartItemId;
        this.cartId = cartId;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    // ==================== Getters and Setters ====================

    public Integer getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(Integer cartItemId) {
        this.cartItemId = cartItemId;
    }

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    // ==================== Helper Methods ====================

    /**
     * Calculates subtotal for this cart item.
     *
     * @return productPrice * quantity
     */
    public BigDecimal calculateSubtotal() {
        if (productPrice == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return productPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
