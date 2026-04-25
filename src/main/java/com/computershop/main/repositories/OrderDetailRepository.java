package com.computershop.main.repositories;

import com.computershop.main.entities.OrderDetail;
import com.computershop.main.entities.Order;
import com.computershop.main.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    
    List<OrderDetail> findByOrder(Order order);
    
    List<OrderDetail> findByOrderOrderId(Integer orderId);
    
    List<OrderDetail> findByProduct(Product product);

    List<OrderDetail> findByProductProductId(Integer productId);
    
    List<OrderDetail> findByQuantityGreaterThan(Integer minQuantity);

    List<OrderDetail> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    @Query("SELECT SUM(od.price * od.quantity) FROM OrderDetail od WHERE od.product.productId = :productId")
    BigDecimal calculateTotalRevenueByProduct(@Param("productId") Integer productId);

    @Query("SELECT SUM(od.quantity) FROM OrderDetail od WHERE od.product.productId = :productId")
    Long calculateTotalQuantitySoldByProduct(@Param("productId") Integer productId);
    
    @Query("SELECT od.product, SUM(od.quantity) as totalSold FROM OrderDetail od " +
           "GROUP BY od.product ORDER BY totalSold DESC")
    List<Object[]> findBestSellingProductsByQuantity();
    
    @Query("SELECT od.product, SUM(od.price * od.quantity) as totalRevenue FROM OrderDetail od " +
           "GROUP BY od.product ORDER BY totalRevenue DESC")
    List<Object[]> findBestSellingProductsByRevenue();
    
    @Query("SELECT SUM(od.price * od.quantity) FROM OrderDetail od WHERE od.order.orderId = :orderId")
    BigDecimal calculateOrderTotal(@Param("orderId") Integer orderId);
    
    @Query("SELECT COUNT(od) FROM OrderDetail od WHERE od.order.orderId = :orderId")
    long countItemsInOrder(@Param("orderId") Integer orderId);
    
    @Query("SELECT od, od.product.productName, od.product.price FROM OrderDetail od WHERE od.order.orderId = :orderId")
    List<Object[]> findOrderDetailsWithProductInfo(@Param("orderId") Integer orderId);

    // Get all order details for a user (for "My Ordered Products" feature)
    @Query("SELECT od FROM OrderDetail od JOIN FETCH od.product p JOIN FETCH od.order o JOIN FETCH o.user u WHERE u.userId = :userId ORDER BY o.orderDate DESC")
    List<OrderDetail> findAllByUserId(@Param("userId") Integer userId);

    // Get all ordered products (distinct products) for a user with total quantity ordered
    @Query("SELECT od.product, SUM(od.quantity) as totalQuantity, MAX(od.order.orderDate) as lastOrderDate " +
           "FROM OrderDetail od JOIN od.order o JOIN o.user u " +
           "WHERE u.userId = :userId " +
           "GROUP BY od.product ORDER BY lastOrderDate DESC")
    List<Object[]> findOrderedProductsSummaryByUserId(@Param("userId") Integer userId);
}