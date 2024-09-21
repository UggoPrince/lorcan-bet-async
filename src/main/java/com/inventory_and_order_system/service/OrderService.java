package com.inventory_and_order_system.service;

import com.inventory_and_order_system.dto.request.OrderDto;
import com.inventory_and_order_system.enums.Status;
import com.inventory_and_order_system.exception.NotFoundException;
import com.inventory_and_order_system.model.Inventory;
import com.inventory_and_order_system.model.Order;
import com.inventory_and_order_system.model.OrderLog;
import com.inventory_and_order_system.model.Product;
import com.inventory_and_order_system.repository.OrderLogsRepository;
import com.inventory_and_order_system.repository.OrderRepository;
import com.inventory_and_order_system.repository.ProductRepository;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private OrderLogsRepository orderLogRepository;
    private InventoryService inventoryService;
    private RetryTemplate retryTemplate;  // For retry mechanism
    private KafkaTemplate<String, String> kafkaTemplate;

    private PaymentService paymentService;
    private ProductService productService;
    private static final int MAX_RETRIES = 3;

    @Autowired
    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            OrderLogsRepository orderLogRepository,
            InventoryService inventoryService,
            RetryTemplate retryTemplate,
            KafkaTemplate<String, String> kafkaTemplate,
            PaymentService paymentService,
            ProductService productService
    ) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderLogRepository = orderLogRepository;
        this.inventoryService = inventoryService;
        this.retryTemplate = retryTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.paymentService = paymentService;
        this.productService = productService;
    }

//    @Transactional
//    public void processOrder(Order order) {
//        try {
//            retryTemplate.execute(context -> {
//                return attemptToProcessOrder(order);
//            });
//        } catch (Exception e) {
//            logOrderFailure(order, e.getMessage());
//        }
//    }

    private Boolean attemptToProcessOrder(Order order) throws Exception {

        Product product = productRepository.findById(order.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Optimistic locking: Version control will ensure concurrency safety
        if (inventoryService.checkStock(order.getProductId(), order.getQuantity())) {
            inventoryService.deductStock(order.getProductId(), order.getQuantity());
        } else {
            throw new RuntimeException("Insufficient stock");
        }

        order.setStatus(Status.PROCESSED.name());
        orderRepository.save(order);
        logOrderSuccess(order);
        return true;
    }

    private void logOrderSuccess(Order order) {
        OrderLog log = new OrderLog();
        log.setOrderId(order.getId());
        log.setStatus(Status.PROCESSED.name());
        log.setProcessedAt(LocalDateTime.now());
        log.setErrorMessage("");
        orderLogRepository.save(log);
    }

    private void logOrderFailure(Order order, String errorMessage) {
        OrderLog log = new OrderLog();
        log.setOrderId(order.getId());
        log.setStatus(Status.FAILED.name());
        log.setErrorMessage(Objects.requireNonNullElse(errorMessage, "Order failed."));
        log.setProcessedAt(LocalDateTime.now());
        orderLogRepository.save(log);
    }


    @Transactional
    public Order createOrder(OrderDto orderDto) {
        Order order = new Order();
        order.setStatus(Status.PENDING.name());
        order.setProductId(orderDto.getProductId());
        order.setQuantity(orderDto.getQuantity());
        order = orderRepository.save(order);

        // Send order creation event to Kafka or RabbitMQ for async processing
        kafkaTemplate.send("order-events", order.getId().toString());
        return order;
    }

    @KafkaListener(topics = "order-events")
    public void processOrder(String orderId) {
        Order order = orderRepository.findById(Long.parseLong(orderId))
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));
        log.info("PROCESSING ORDER: {}", order);

        Product product = productService.getProduct(order.getProductId()).get();

        double amount = product.getPrice() * order.getQuantity();

        initiatePaymentProcess(order, amount, 0);
    }

    private void initiatePaymentProcess(Order order, double amount, int attempt) {
        CompletableFuture<Boolean> paid = paymentService.processPayment(order.getId(), amount);

        paid.thenApply(paymentSuccessful -> {
            if (paymentSuccessful) {
                completeOrder(order);
            } else if (attempt < MAX_RETRIES) {
                retryPayment(order, amount, attempt + 1);
            } else {
                // Handle payment failure
                handlePaymentFailure(order, "Could not complete payment");
            }
            return paymentSuccessful;
        }).exceptionally(ex -> {
            // Handle exceptions during payment processing (e.g., network failure)
            if (attempt < MAX_RETRIES) {
                retryPayment(order, amount, attempt + 1);
            } else {
                handlePaymentError(order, ex);
            }
            return false;
        });
    }

    private void retryPayment(Order order, double amount, int attempt) {
        log.info("Retrying payment for order {} (Attempt {})", order.getId(), attempt);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        initiatePaymentProcess(order, amount, attempt);
    }

    private void handlePaymentError(Order order, Throwable ex) {
        order.setStatus(Status.FAILED.name());
        orderRepository.save(order);
        logOrderFailure(order, ex.getMessage());
    }

    private void handlePaymentFailure(Order order, String errorMessage) {
        order.setStatus(Status.FAILED.name());
        orderRepository.save(order);
        logOrderFailure(order, errorMessage);
    }

    private void completeOrder(Order order) {
        inventoryService.deductStock(order.getProductId(), order.getQuantity());
        order.setStatus(Status.PROCESSED.name());
        orderRepository.save(order);
        logOrderSuccess(order);
        log.info("ORDER PROCESSED");
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
