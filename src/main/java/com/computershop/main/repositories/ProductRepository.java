package com.computershop.main.repositories;

import com.computershop.main.entities.Product;

import com.computershop.main.entities.Category;
import com.computershop.main.entities.Image;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    List<Product> findByProductNameContainingIgnoreCase(String keyword);
    
    List<Product> findByDescriptionContainingIgnoreCase(String keyword);
    
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Product> findByStockQuantityGreaterThan(Integer minStock);
    
    List<Product> findByImage(Image image);
    
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0")
    List<Product> findInStockProducts();
    
    @Query("SELECT p FROM Product p WHERE p.stockQuantity = 0")
    List<Product> findOutOfStockProducts();
    
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(@Param("keyword") String keyword);
    
    @Query("SELECT p FROM Product p JOIN OrderDetail od ON p.productId = od.product.productId " +
           "GROUP BY p.productId ORDER BY SUM(od.quantity) DESC")
    List<Product> findTopSellingProducts();
    
    List<Product> findAllByOrderByPriceAsc();
    
    List<Product> findAllByOrderByPriceDesc();
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity > 0")
    long countInStockProducts();
    
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0 ORDER BY p.createdAt DESC")
    List<Product> findFeaturedProducts(Pageable pageable);
    
    @Query("SELECT DISTINCT c FROM Category c JOIN c.products p")
    List<Category> findDistinctCategories();
    
    List<Product> findByCategory(Category category);
    
    List<Product> findByCategoryCategoryId(Integer categoryId);
    
    @Query("SELECT p FROM Product p WHERE p.stockQuantity < 20 ORDER BY p.stockQuantity ASC")
    List<Product> findLowStockProducts(Pageable pageable);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.image")
    List<Product> findAllWithCategoryAndImage();
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.image WHERE p.stockQuantity > 0 ORDER BY p.createdAt DESC")
    List<Product> findFeaturedProductsWithDetails();
}