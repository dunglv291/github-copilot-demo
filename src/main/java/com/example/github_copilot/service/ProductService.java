package com.example.github_copilot.service;

import com.example.github_copilot.dto.ProductRequestDTO;
import com.example.github_copilot.dto.ProductResponseDTO;
import com.example.github_copilot.mapper.ProductMapper;
import com.example.github_copilot.model.Product;
import com.example.github_copilot.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return productMapper.toResponseDTO(product);
    }

    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Product product = productMapper.toEntity(productRequestDTO);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(savedProduct);
    }

    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        Product updatedProduct = productMapper.updateEntityFromDTO(productRequestDTO, existing);
        Product savedProduct = productRepository.save(updatedProduct);
        return productMapper.toResponseDTO(savedProduct);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    public List<ProductResponseDTO> getProductsByCategory(String category) {
        return productRepository.findByCategoryOrderByPriceAsc(category)
                .stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}