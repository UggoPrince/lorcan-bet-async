package com.inventory_and_order_system.repository;

import com.inventory_and_order_system.model.OrderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderLogsRepository extends JpaRepository<OrderLog, Long> {
}
