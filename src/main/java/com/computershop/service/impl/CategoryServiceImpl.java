package com.computershop.service.impl;

import com.computershop.main.entities.Category;
import com.computershop.main.repositories.CategoryRepository;
import com.computershop.service.api.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of CategoryService.
 * Handles all category-related business logic.
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> getCategoryById(Integer categoryId) {
        return categoryRepository.findById(categoryId);
    }

    @Override
    public Optional<Category> getCategoryByName(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName);
    }

    @Override
    public List<Category> getAllCategoriesOrderedByName() {
        return categoryRepository.findAllByOrderByCategoryNameAsc();
    }

    @Override
    public Category createCategory(Category category) {
        if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
            throw new RuntimeException("Category '" + category.getCategoryName() + "' already exists");
        }
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Integer categoryId, Category categoryDetails) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        category.setCategoryName(categoryDetails.getCategoryName());
        category.setDescription(categoryDetails.getDescription());

        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Integer categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Category not found with id: " + categoryId);
        }
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public boolean existsByCategoryName(String categoryName) {
        return categoryRepository.existsByCategoryName(categoryName);
    }
}
