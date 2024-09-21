package com.inventory_and_order_system.controller;

import com.inventory_and_order_system.dto.request.OrderDto;
import com.inventory_and_order_system.exception.BadRequestException;
import com.inventory_and_order_system.model.Order;
import com.inventory_and_order_system.service.InventoryService;
import com.inventory_and_order_system.service.OrderService;
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
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    private final InventoryService inventoryService;

    private final ProductService productService;



    @Autowired
    public OrderController(OrderService orderService, InventoryService inventoryService, ProductService productService) {
        this.orderService = orderService;
        this.inventoryService = inventoryService;
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderDto order) throws BadRequestException {
        if (productService.getProduct(order.getProductId()).isEmpty()) {
            throw new ResourceNotFoundException("Product not found.");
        }
        if (!inventoryService.checkStock(order.getProductId(), order.getQuantity())) {
            throw new BadRequestException("Not enough quantity left.");
        }
        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }
}
