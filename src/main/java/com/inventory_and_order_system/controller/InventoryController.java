package com.inventory_and_order_system.controller;

import com.inventory_and_order_system.dto.request.InventoryDto;
import com.inventory_and_order_system.model.Inventory;
import com.inventory_and_order_system.service.InventoryService;
import com.inventory_and_order_system.service.ProductService;
import jakarta.validation.Valid;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventories")
public class InventoryController {
    private final InventoryService inventoryService;
    private final ProductService productService;

    @Autowired
    public InventoryController(InventoryService inventoryService, ProductService productService) {
        this.inventoryService = inventoryService;
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Inventory> createInventory(@Valid @RequestBody InventoryDto inventoryDto) {
        if (productService.getProduct(inventoryDto.getProductId()).isEmpty()) {
            throw new ResourceNotFoundException("Product for this inventory does not exist");
        }
        Inventory inventory =  inventoryService.addStock(inventoryDto.getProductId(), inventoryDto.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(inventory);
    }
}
