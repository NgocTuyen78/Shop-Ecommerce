package com.computershop.service.impl;

import com.computershop.main.entities.Product;
import com.computershop.main.entities.Image;
import com.computershop.main.entities.Category;
import com.computershop.main.repositories.ProductRepository;
import com.computershop.service.api.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of ProductService.
 * Handles all product-related business logic.
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAllWithCategoryAndImage();
    }

    @Override
    public Optional<Product> getProductById(Integer productId) {
        return productRepository.findById(productId);
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword);
    }

    @Override
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    @Override
    public List<Product> getInStockProducts() {
        return productRepository.findInStockProducts();
    }

    @Override
    public List<Product> getProductsByPriceAsc() {
        return productRepository.findAllByOrderByPriceAsc();
    }

    @Override
    public List<Product> getProductsByPriceDesc() {
        return productRepository.findAllByOrderByPriceDesc();
    }

    @Override
    public Product createProduct(Product product) {
        // Set default image if not provided
        if (product.getImage() == null) {
            // TODO: Inject ImageService when available
            // product.setImage(imageService.getPlaceholderImage());
        }
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Integer productId, Product productDetails) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        product.setProductName(productDetails.getProductName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStockQuantity(productDetails.getStockQuantity());

        if (productDetails.getImage() != null) {
            product.setImage(productDetails.getImage());
        }

        return productRepository.save(product);
    }

    @Override
    public Product updateProductStock(Integer productId, Integer newStock) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        product.setStockQuantity(newStock);
        return productRepository.save(product);
    }

    @Override
    public Product decreaseStock(Integer productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getStockQuantity() + ", Requested: " + quantity);
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        return productRepository.save(product);
    }

    public Product increaseStock(Integer productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        product.setStockQuantity(product.getStockQuantity() + quantity);
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found with id: " + productId);
        }
        productRepository.deleteById(productId);
    }

    @Override
    public boolean isInStock(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        return product.getStockQuantity() > 0;
    }

    @Override
    public boolean hasSufficientStock(Integer productId, Integer requestedQuantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        return product.getStockQuantity() >= requestedQuantity;
    }

    @Override
    public List<Product> getFeaturedProducts(int limit) {
        List<Product> allFeatured = productRepository.findFeaturedProductsWithDetails();
        return allFeatured.stream().limit(limit).toList();
    }

    @Override
    public long getTotalProducts() {
        return productRepository.count();
    }

    @Override
    public List<Category> getAllCategories() {
        return productRepository.findDistinctCategories();
    }

    @Override
    public List<String> getAllCategoryNames() {
        return getAllCategories().stream()
                .map(Category::getCategoryName)
                .toList();
    }

    @Override
    public List<Product> getProductsByCategory(Category category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public List<Product> getProductsByCategoryName(String categoryName) {
        return productRepository.findDistinctCategories().stream()
                .filter(cat -> cat.getCategoryName().equals(categoryName))
                .findFirst()
                .map(this::getProductsByCategory)
                .orElse(List.of());
    }

    @Override
    public List<Product> getLowStockProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findLowStockProducts(pageable);
    }
}
