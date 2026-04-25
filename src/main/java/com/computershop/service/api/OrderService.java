package com.computershop.service.api;

import com.computershop.main.entities.Order;
import com.computershop.main.entities.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Order operations.
 * Defines the contract for order-related business logic.
 */
public interface OrderService {

    /**
     * Retrieves all orders.
     *
     * @return list of all orders
     */
    List<Order> getAllOrders();

    /**
     * Retrieves an order by its ID.
     *
     * @param orderId the order ID
     * @return optional containing the order if found
     */
    Optional<Order> getOrderById(Integer orderId);

    /**
     * Retrieves orders by user.
     *
     * @param user the user
     * @return list of orders for the user
     */
    List<Order> getOrdersByUser(User user);

    /**
     * Retrieves orders by user ID.
     *
     * @param userId the user ID
     * @return list of orders for the user
     */
    List<Order> getOrdersByUserId(Integer userId);

    /**
     * Retrieves orders by status.
     *
     * @param status the order status
     * @return list of orders with the specified status
     */
    List<Order> getOrdersByStatus(String status);

    /**
     * Creates a new order.
     *
     * @param order the order to create
     * @return the created order
     */
    Order createOrder(Order order);

    /**
     * Updates an existing order.
     *
     * @param orderId the order ID
     * @param order the order with updated details
     * @return the updated order
     */
    Order updateOrder(Integer orderId, Order order);

    /**
     * Updates the status of an order.
     *
     * @param orderId the order ID
     * @param status the new status
     */
    void updateOrderStatus(Integer orderId, String status);

    /**
     * Deletes an order by its ID.
     *
     * @param orderId the order ID
     */
    void deleteOrder(Integer orderId);

    /**
     * Retrieves total number of orders.
     *
     * @return total order count
     */
    long getTotalOrders();

    /**
     * Retrieves total revenue from all orders.
     *
     * @return total revenue
     */
    double getTotalRevenue();

    /**
     * Retrieves recent orders with a limit.
     *
     * @param limit the maximum number of orders
     * @return list of recent orders
     */
    List<Order> getRecentOrders(int limit);

    /**
     * Retrieves pending orders.
     *
     * @return list of pending orders
     */
    List<Order> getPendingOrders();

    /**
     * Retrieves completed orders.
     *
     * @return list of completed orders
     */
    List<Order> getCompletedOrders();

    /**
     * Retrieves all ordered products for a user.
     * Returns product details along with total quantity ordered and last order date.
     *
     * @param userId the user ID
     * @return list of objects containing product, total quantity, and last order date
     */
    List<Object[]> getOrderedProductsByUserId(Integer userId);
}
