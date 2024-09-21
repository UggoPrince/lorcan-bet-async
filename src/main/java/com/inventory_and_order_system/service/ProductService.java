package com.inventory_and_order_system.service;

import com.inventory_and_order_system.dto.request.ProductDto;
import com.inventory_and_order_system.dto.request.UpdateProductDto;
import com.inventory_and_order_system.model.Product;
import com.inventory_and_order_system.repository.ProductRepository;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {
    private ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Optional<Product> getProduct(Long id) {
        return productRepository.findById(id);
    }

    public Product createProduct(ProductDto productDto) {
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        return productRepository.save(product);
    }

    public Product updateProduct(Long productId, UpdateProductDto updatedProductDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (updatedProductDto.getName() != null) {
            product.setName(updatedProductDto.getName());
        }
        if (updatedProductDto.getDescription() != null) {
            product.setDescription(updatedProductDto.getDescription());
        }
        if (updatedProductDto.getPrice() != null) {
            product.setPrice(updatedProductDto.getPrice());
        }
        return productRepository.save(product);
    }

    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

}
