package com.computershop.main.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.computershop.main.entities.Order;
import com.computershop.main.entities.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    
    List<Order> findByUser(User user);
    
    List<Order> findByUserUserId(Integer userId);
    
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Order> findByUserOrderByOrderDateDesc(User user);
    
    List<Order> findByUserUserIdOrderByOrderDateDesc(Integer userId);
    
    @Query("SELECT o FROM Order o WHERE o.orderDate >= :startOfDay AND o.orderDate < :endOfDay")
    List<Order> findTodayOrders(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
    
    @Query("SELECT o FROM Order o WHERE o.orderDate >= :startDate")
    List<Order> findOrdersFromLastDays(@Param("startDate") LocalDateTime startDate);
    
    long countByUser(User user);
    
    long countByUserUserId(Integer userId);
    
    @Query("SELECT o, SUM(od.price * od.quantity) as totalAmount FROM Order o " +
           "JOIN o.orderDetails od GROUP BY o.orderId")
    List<Object[]> findOrdersWithTotalAmount();
    
    @Query("SELECT o FROM Order o WHERE o.orderDate >= :thirtyDaysAgo ORDER BY o.orderDate DESC")
    List<Order> findRecentOrders(@Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);
    
    @Query("SELECT o FROM Order o WHERE o.user.userId = :userId ORDER BY o.orderDate DESC")
    List<Order> findRecentOrdersByUserId(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderDetails od LEFT JOIN FETCH od.product WHERE o.user.userId = :userId ORDER BY o.orderDate DESC")
    List<Order> findByUserUserIdWithDetailsFetched(@Param("userId") Integer userId);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderDetails od LEFT JOIN FETCH od.product p LEFT JOIN FETCH p.image LEFT JOIN FETCH p.category WHERE o.orderId = :orderId")
    java.util.Optional<Order> findByIdWithDetails(@Param("orderId") Integer orderId);

    @Query("SELECT COALESCE(SUM(od.price * od.quantity), 0) FROM Order o " +
           "JOIN o.orderDetails od WHERE o.user.userId = :userId")
    double getTotalSpentByUserId(@Param("userId") Integer userId);
    
    @Query("SELECT o FROM Order o ORDER BY o.orderDate DESC")
    List<Order> findRecentOrdersForAdmin(Pageable pageable);

    List<Order> findByStatus(String status);
}