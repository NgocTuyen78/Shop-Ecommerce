package com.computershop.dto.mapper;

import com.computershop.dto.*;
import com.computershop.main.entities.*;

import java.math.BigDecimal;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class to convert between Entity and DTO objects.
 * Provides static methods for bidirectional conversion.
 */
public final class EntityMapper {

    // ==================== Private Constructor ====================
    private EntityMapper() {
        throw new UnsupportedOperationException("EntityMapper class cannot be instantiated");
    }

    // ==================== Product Mappers ====================

    /**
     * Converts a Product entity to ProductDTO.
     *
     * @param product the Product entity
     * @return ProductDTO
     */
    public static ProductDTO toProductDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setCreatedAt(product.getCreatedAt());

        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getCategoryId());
            dto.setCategoryName(product.getCategory().getCategoryName());
        }

        if (product.getImage() != null) {
            dto.setImageId(product.getImage().getImageId());
            dto.setImageUrl(product.getImage().getImageUrl());
        }

        return dto;
    }

    /**
     * Converts a list of Product entities to ProductDTOs.
     *
     * @param products list of Product entities
     * @return list of ProductDTOs
     */
    public static List<ProductDTO> toProductDTOs(List<Product> products) {
        if (products == null) {
            return null;
        }
        return products.stream()
                .map(EntityMapper::toProductDTO)
                .collect(Collectors.toList());
    }

    // ==================== User Mappers ====================

    /**
     * Converts a User entity to UserDTO.
     *
     * @param user the User entity
     * @return UserDTO
     */
    public static UserDTO toUserDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(null);

        if (user.getRole() != null) {
            dto.setRoleName(user.getRole().getRoleName());
        }

        return dto;
    }

    /**
     * Converts a list of User entities to UserDTOs.
     *
     * @param users list of User entities
     * @return list of UserDTOs
     */
    public static List<UserDTO> toUserDTOs(List<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(EntityMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    // ==================== Category Mappers ====================

    /**
     * Converts a Category entity to CategoryDTO.
     *
     * @param category the Category entity
     * @return CategoryDTO
     */
    public static CategoryDTO toCategoryDTO(Category category) {
        if (category == null) {
            return null;
        }

        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryId(category.getCategoryId());
        dto.setCategoryName(category.getCategoryName());
        dto.setDescription(category.getDescription());
        dto.setCreatedAt(category.getCreatedAt());

        return dto;
    }

    /**
     * Converts a list of Category entities to CategoryDTOs.
     *
     * @param categories list of Category entities
     * @return list of CategoryDTOs
     */
    public static List<CategoryDTO> toCategoryDTOs(List<Category> categories) {
        if (categories == null) {
            return null;
        }
        return categories.stream()
                .map(EntityMapper::toCategoryDTO)
                .collect(Collectors.toList());
    }

    // ==================== Order Mappers ====================

    /**
     * Converts an Order entity to OrderDTO.
     *
     * @param order the Order entity
     * @return OrderDTO
     */
    public static OrderDTO toOrderDTO(Order order) {
        if (order == null) {
            return null;
        }

        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(BigDecimal.valueOf(order.getTotalAmount()));

        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getUserId());
            dto.setUsername(order.getUser().getUsername());
            dto.setUserEmail(null);
        }

        if (order.getOrderDetails() != null) {
            List<OrderDTO.OrderDetailDTO> details = order.getOrderDetails().stream()
                    .map(EntityMapper::toOrderDetailDTO)
                    .collect(Collectors.toList());
            dto.setOrderDetails(details);
        }

        return dto;
    }

    /**
     * Converts an OrderDetail entity to OrderDetailDTO.
     *
     * @param detail the OrderDetail entity
     * @return OrderDetailDTO
     */
    public static OrderDTO.OrderDetailDTO toOrderDetailDTO(OrderDetail detail) {
        if (detail == null) {
            return null;
        }

        OrderDTO.OrderDetailDTO dto = new OrderDTO.OrderDetailDTO();
        dto.setOrderDetailId(detail.getOrderDetailId());
        dto.setQuantity(detail.getQuantity());
        dto.setPrice(detail.getPrice());

        if (detail.getProduct() != null) {
            dto.setProductId(detail.getProduct().getProductId());
            dto.setProductName(detail.getProduct().getProductName());
        }

        return dto;
    }

    /**
     * Converts a list of Order entities to OrderDTOs.
     *
     * @param orders list of Order entities
     * @return list of OrderDTOs
     */
    public static List<OrderDTO> toOrderDTOs(List<Order> orders) {
        if (orders == null) {
            return null;
        }
        return orders.stream()
                .map(EntityMapper::toOrderDTO)
                .collect(Collectors.toList());
    }

    // ==================== CartItem Mappers ====================

    /**
     * Converts a CartItem entity to CartItemDTO.
     *
     * @param cartItem the CartItem entity
     * @return CartItemDTO
     */
    public static CartItemDTO toCartItemDTO(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }

        CartItemDTO dto = new CartItemDTO();
        dto.setCartItemId(cartItem.getCartItemId());
        dto.setQuantity(cartItem.getQuantity());

        if (cartItem.getCart() != null) {
            dto.setCartId(cartItem.getCart().getCartId());
        }

        if (cartItem.getProduct() != null) {
            dto.setProductId(cartItem.getProduct().getProductId());
            dto.setProductName(cartItem.getProduct().getProductName());
            dto.setProductPrice(cartItem.getProduct().getPrice());
            if (cartItem.getProduct().getImage() != null) {
                dto.setProductImageUrl(cartItem.getProduct().getImage().getImageUrl());
            }
            dto.setSubtotal(dto.calculateSubtotal());
        }

        return dto;
    }

    /**
     * Converts a list of CartItem entities to CartItemDTOs.
     *
     * @param cartItems list of CartItem entities
     * @return list of CartItemDTOs
     */
    public static List<CartItemDTO> toCartItemDTOs(List<CartItem> cartItems) {
        if (cartItems == null) {
            return null;
        }
        return cartItems.stream()
                .map(EntityMapper::toCartItemDTO)
                .collect(Collectors.toList());
    }
}
