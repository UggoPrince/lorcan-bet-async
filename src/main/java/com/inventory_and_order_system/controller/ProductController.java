package com.inventory_and_order_system.controller;

import com.inventory_and_order_system.dto.request.ProductDto;
import com.inventory_and_order_system.dto.request.UpdateProductDto;
import com.inventory_and_order_system.exception.NotFoundException;
import com.inventory_and_order_system.model.Product;
import com.inventory_and_order_system.service.InventoryService;
import com.inventory_and_order_system.service.ProductService;
import com.inventory_and_order_system.validation.NotEmptyBody;
import com.inventory_and_order_system.validation.ValidId;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductDto product) {
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@ValidId @PathVariable Long id, @Valid @NotEmptyBody @RequestBody UpdateProductDto productDto) {
        Product updatedProduct = productService.updateProduct(id, productDto);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@ValidId @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}
