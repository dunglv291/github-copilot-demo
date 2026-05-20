package com.example.github_copilot.service;

import com.example.github_copilot.model.Product;
import com.example.github_copilot.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;
    private Product product3;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "Laptop", "High Performance", 999.99, "Electronics");
        product2 = new Product(2L, "Mouse", "Wireless", 29.99, "Electronics");
        product3 = new Product(3L, "Desk", "Wooden", 299.99, "Furniture");
    }

    @Test
    @DisplayName("Should get all products successfully")
    void testGetAllProducts_Success() {
        // Arrange
        List<Product> products = Arrays.asList(product1, product2, product3);
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(product1, result.get(0));
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get product by id successfully")
    void testGetProductById_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        // Act
        Product result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Laptop", result.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when product not found by id")
    void testGetProductById_NotFound() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> productService.getProductById(999L));
        assertTrue(assertThrows(RuntimeException.class,
                () -> productService.getProductById(999L)).getMessage()
                .contains("Product not found with id"));
        verify(productRepository, times(2)).findById(anyLong());
    }

    @Test
    @DisplayName("Should create product successfully")
    void testCreateProduct_Success() {
        // Arrange
        when(productRepository.save(product1)).thenReturn(product1);

        // Act
        Product result = productService.createProduct(product1);

        // Assert
        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals(999.99, result.getPrice());
        verify(productRepository, times(1)).save(product1);
    }

    @Test
    @DisplayName("Should update product successfully")
    void testUpdateProduct_Success() {
        // Arrange
        Product updatedProduct = new Product(1L, "Gaming Laptop", "RTX 3080", 1499.99, "Electronics");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        Product result = productService.updateProduct(1L, updatedProduct);

        // Assert
        assertNotNull(result);
        assertEquals("Gaming Laptop", result.getName());
        assertEquals("RTX 3080", result.getDescription());
        assertEquals(1499.99, result.getPrice());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent product")
    void testUpdateProduct_NotFound() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> productService.updateProduct(999L, product1));
        verify(productRepository, times(1)).findById(anyLong());
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete product successfully")
    void testDeleteProduct_Success() {
        // Arrange
        doNothing().when(productRepository).deleteById(1L);

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should get products by category sorted by price ascending")
    void testGetProductsByCategory_Success() {
        // Arrange
        List<Product> electronicsProducts = Arrays.asList(product2, product1); // sorted by price asc
        when(productRepository.findByCategoryOrderByPriceAsc("Electronics"))
                .thenReturn(electronicsProducts);

        // Act
        List<Product> result = productService.getProductsByCategory("Electronics");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(29.99, result.get(0).getPrice()); // lowest price first
        assertEquals(999.99, result.get(1).getPrice()); // highest price last
        assertEquals("Mouse", result.get(0).getName());
        assertEquals("Laptop", result.get(1).getName());
        verify(productRepository, times(1)).findByCategoryOrderByPriceAsc("Electronics");
    }

    @Test
    @DisplayName("Should return empty list when no products found for category")
    void testGetProductsByCategory_EmptyResult() {
        // Arrange
        when(productRepository.findByCategoryOrderByPriceAsc("NonExistent"))
                .thenReturn(List.of());

        // Act
        List<Product> result = productService.getProductsByCategory("NonExistent");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findByCategoryOrderByPriceAsc("NonExistent");
    }
}