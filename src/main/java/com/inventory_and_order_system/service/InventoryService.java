package com.inventory_and_order_system.service;

import com.inventory_and_order_system.model.Inventory;
import com.inventory_and_order_system.model.Order;
import com.inventory_and_order_system.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class InventoryService {
    private InventoryRepository inventoryRepository;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    private static final int MAX_RETRIES = 3;

    // Check if the product has enough stock
    public boolean checkStock(Long productId, Integer quantity) {
        Optional<Inventory> inventory = inventoryRepository.findByProductId(productId);
        return inventory.isPresent() && inventory.get().getQuantity() >= quantity;
    }

    // gets a stock by product id
    public Optional<Inventory> getStock(Long productId) {
        Optional<Inventory> inventory = inventoryRepository.findByProductId(productId);
        return inventory;
    }

    // Deduct stock from inventory
    @Transactional
    public boolean deductStock(Long productId, Integer quantity) {
        int attempt = 0;
        Optional<Inventory> inventoryOptional = inventoryRepository.findByProductId(productId);

//        if (inventoryOptional.isPresent()) {
//            Inventory inventory = inventoryOptional.get();
//            int currentStock = inventory.getQuantity();
//
//            if (currentStock >= quantity) {
//                inventory.setQuantity(currentStock - quantity);
//                inventoryRepository.save(inventory);
//                return true;
//            } else {
//                return false; // Not enough stock
//            }
//        }
//
//        return false; // Product not found

        while (attempt < MAX_RETRIES) {
            try {
                // Optional<Inventory> inventoryOptional = inventoryRepository.findByProductId(productId);

                if (inventoryOptional.isPresent()) {
                    Inventory inventory = inventoryOptional.get();
                    int currentStock = inventory.getQuantity();

                    if (currentStock >= quantity) {
                        inventory.setQuantity(currentStock - quantity);
                        inventoryRepository.save(inventory);
                        return true; // Successfully deducted stock
                    } else {
                        return false; // Not enough stock
                    }
                }

                return false; // Product not found
            } catch (ObjectOptimisticLockingFailureException e) {
                attempt++;
                if (attempt >= MAX_RETRIES) {
                    throw new RuntimeException("Failed to update inventory after multiple attempts due to concurrent modification.");
                }
            }
        }

        return false;
    }

    // Add stock to inventory (could be used when a re-stock occurs)
    @Transactional
    public Inventory addStock(Long productId, Integer quantity) {
        Optional<Inventory> inventoryOptional = inventoryRepository.findByProductId(productId);

        if (inventoryOptional.isPresent()) {
            Inventory inventory = inventoryOptional.get();
            inventory.setQuantity(inventory.getQuantity() + quantity);
            inventoryRepository.save(inventory);
            return inventory;
        } else {
            // If product doesn't exist in inventory, create new record
            Inventory inventory = new Inventory();
            inventory.setProductId(productId);
            inventory.setQuantity(quantity);
            inventoryRepository.save(inventory);
            return inventory;
        }
    }

    @Transactional
    public boolean reserveInventory(Order order) {
        Optional<Inventory> inventoryOptional = inventoryRepository.findByProductId(order.getProductId());

        if (inventoryOptional.isPresent()) {
            Inventory inventory = inventoryOptional.get();
            int availableStock = inventory.getQuantity() - inventory.getReservedQuantity();

            if (availableStock >= order.getQuantity()) {
                inventory.setReservedQuantity(inventory.getReservedQuantity() + order.getQuantity());
                inventoryRepository.save(inventory);
            } else {
                return false; // Not enough stock to reserve
            }
        } else {
            return false; // Product not found
        }
        return true;
    }

    @Transactional
    public void confirmInventory(Order order) {
        Optional<Inventory> inventoryOptional = inventoryRepository.findByProductId(order.getProductId());

        if (inventoryOptional.isPresent()) {
            Inventory inventory = inventoryOptional.get();
            inventory.setQuantity(inventory.getQuantity() - order.getQuantity());
            inventory.setReservedQuantity(inventory.getReservedQuantity() - order.getQuantity());
            inventoryRepository.save(inventory);
        }
    }

    @Transactional
    public void releaseReservedInventory(Order order) {
        Optional<Inventory> inventoryOptional = inventoryRepository.findByProductId(order.getProductId());

        if (inventoryOptional.isPresent()) {
            Inventory inventory = inventoryOptional.get();
            inventory.setReservedQuantity(inventory.getReservedQuantity() - order.getQuantity());
            inventoryRepository.save(inventory);
        }
    }
}
