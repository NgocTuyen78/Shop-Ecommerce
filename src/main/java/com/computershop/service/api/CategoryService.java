package com.computershop.service.api;

import com.computershop.main.entities.Category;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Category operations.
 * Defines the contract for category-related business logic.
 */
public interface CategoryService {

    /**
     * Retrieves all categories.
     *
     * @return list of all categories
     */
    List<Category> getAllCategories();

    /**
     * Retrieves a category by its ID.
     *
     * @param categoryId the category ID
     * @return optional containing the category if found
     */
    Optional<Category> getCategoryById(Integer categoryId);

    /**
     * Retrieves a category by its name.
     *
     * @param categoryName the category name
     * @return optional containing the category if found
     */
    Optional<Category> getCategoryByName(String categoryName);

    /**
     * Retrieves all categories ordered by name.
     *
     * @return list of categories sorted by name
     */
    List<Category> getAllCategoriesOrderedByName();

    /**
     * Creates a new category.
     *
     * @param category the category to create
     * @return the created category
     */
    Category createCategory(Category category);

    /**
     * Updates an existing category.
     *
     * @param categoryId the category ID
     * @param category the category with updated details
     * @return the updated category
     */
    Category updateCategory(Integer categoryId, Category category);

    /**
     * Deletes a category by its ID.
     *
     * @param categoryId the category ID
     */
    void deleteCategory(Integer categoryId);

    /**
     * Checks if a category name exists.
     *
     * @param categoryName the category name
     * @return true if exists
     */
    boolean existsByCategoryName(String categoryName);
}
