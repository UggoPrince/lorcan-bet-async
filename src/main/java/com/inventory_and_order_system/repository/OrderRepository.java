package com.inventory_and_order_system.repository;

import com.inventory_and_order_system.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
