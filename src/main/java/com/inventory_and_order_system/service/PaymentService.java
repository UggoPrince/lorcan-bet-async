package com.inventory_and_order_system.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
public class PaymentService {

    private static final Random RANDOM = new Random();
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    // Simulate an asynchronous payment processing
    public CompletableFuture<Boolean> processPayment(Long orderId, double amount) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(RANDOM.nextInt(2000)); // Simulate delay
                if (RANDOM.nextBoolean()) {
                    log.info("Payment for order {} succeeded.", orderId);
                    return true;
                } else {
                    log.info("Payment for order {} failed.", orderId);
                    return false;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Payment service failed.");
                return false;
            }
        });
    }
}

