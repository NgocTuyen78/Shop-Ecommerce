package com.computershop.main.repositories;

import com.computershop.main.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    
    Optional<Category> findByCategoryName(String categoryName);

    Optional<Category> findByCategoryNameIgnoreCase(String categoryName);
    
    boolean existsByCategoryName(String categoryName);
     
    List<Category> findAllByOrderByCategoryNameAsc();

    @Query("SELECT c.categoryName FROM Category c ORDER BY c.categoryName")
    List<String> findAllCategoryNames();
    
    @Query("SELECT c.categoryName, COUNT(p) FROM Category c LEFT JOIN c.products p GROUP BY c.categoryId, c.categoryName")
    List<Object[]> countProductsInCategories();
}