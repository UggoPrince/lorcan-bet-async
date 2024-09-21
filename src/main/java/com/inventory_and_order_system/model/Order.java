package com.inventory_and_order_system.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("productId")
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @JsonProperty("quantity")
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @JsonProperty("status")
    @Column(name = "status", nullable = false)
    private String status;
}
