package com.example.github_copilot.mapper;

import com.example.github_copilot.dto.ProductRequestDTO;
import com.example.github_copilot.dto.ProductResponseDTO;
import com.example.github_copilot.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    /**
     * Convert ProductRequestDTO to Product entity
     */
    public Product toEntity(ProductRequestDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());
        return product;
    }

    /**
     * Convert Product entity to ProductResponseDTO
     */
    public ProductResponseDTO toResponseDTO(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory()
        );
    }

    /**
     * Update existing Product with ProductRequestDTO data
     */
    public Product updateEntityFromDTO(ProductRequestDTO dto, Product product) {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());
        return product;
    }
}