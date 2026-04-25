package com.computershop.service.api;

import com.computershop.main.entities.Product;
import com.computershop.main.entities.Category;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Product operations.
 * Defines the contract for product-related business logic.
 */
public interface ProductService {

    /**
     * Retrieves all products.
     *
     * @return list of all products
     */
    List<Product> getAllProducts();

    /**
     * Retrieves a product by its ID.
     *
     * @param productId the product ID
     * @return optional containing the product if found
     */
    Optional<Product> getProductById(Integer productId);

    /**
     * Searches products by keyword in name and description.
     *
     * @param keyword the search keyword
     * @return list of matching products
     */
    List<Product> searchProducts(String keyword);

    /**
     * Retrieves products within a price range.
     *
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @return list of products in the price range
     */
    List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Retrieves products that are in stock.
     *
     * @return list of in-stock products
     */
    List<Product> getInStockProducts();

    /**
     * Retrieves products sorted by price ascending.
     *
     * @return list of products sorted by price
     */
    List<Product> getProductsByPriceAsc();

    /**
     * Retrieves products sorted by price descending.
     *
     * @return list of products sorted by price
     */
    List<Product> getProductsByPriceDesc();

    /**
     * Creates a new product.
     *
     * @param product the product to create
     * @return the created product
     */
    Product createProduct(Product product);

    /**
     * Updates an existing product.
     *
     * @param productId the product ID
     * @param product the product with updated details
     * @return the updated product
     */
    Product updateProduct(Integer productId, Product product);

    /**
     * Updates the stock quantity of a product.
     *
     * @param productId the product ID
     * @param newStock the new stock quantity
     * @return the updated product
     */
    Product updateProductStock(Integer productId, Integer newStock);

    /**
     * Decreases the stock quantity of a product.
     *
     * @param productId the product ID
     * @param quantity the quantity to decrease
     * @return the updated product
     */
    Product decreaseStock(Integer productId, Integer quantity);

    /**
     * Deletes a product by its ID.
     *
     * @param productId the product ID
     */
    void deleteProduct(Integer productId);

    /**
     * Checks if a product is in stock.
     *
     * @param productId the product ID
     * @return true if in stock
     */
    boolean isInStock(Integer productId);

    /**
     * Checks if a product has sufficient stock for a requested quantity.
     *
     * @param productId the product ID
     * @param requestedQuantity the requested quantity
     * @return true if sufficient stock
     */
    boolean hasSufficientStock(Integer productId, Integer requestedQuantity);

    /**
     * Retrieves featured products with a limit.
     *
     * @param limit the maximum number of products
     * @return list of featured products
     */
    List<Product> getFeaturedProducts(int limit);

    /**
     * Retrieves total number of products.
     *
     * @return total product count
     */
    long getTotalProducts();

    /**
     * Retrieves all distinct categories.
     *
     * @return list of categories
     */
    List<Category> getAllCategories();

    /**
     * Retrieves all category names.
     *
     * @return list of category names
     */
    List<String> getAllCategoryNames();

    /**
     * Retrieves products by category.
     *
     * @param category the category
     * @return list of products in the category
     */
    List<Product> getProductsByCategory(Category category);

    /**
     * Retrieves products by category name.
     *
     * @param categoryName the category name
     * @return list of products in the category
     */
    List<Product> getProductsByCategoryName(String categoryName);

    /**
     * Retrieves products with low stock.
     *
     * @param limit the maximum number of products
     * @return list of low stock products
     */
    List<Product> getLowStockProducts(int limit);
}
