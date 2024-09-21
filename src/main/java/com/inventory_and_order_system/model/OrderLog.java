package com.inventory_and_order_system.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "order_logs")
public class OrderLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    @JsonProperty("orderId")
    private Long orderId;

    @JsonProperty("status")
    @Column(name = "status", nullable = false)
    private String status;

    @JsonProperty("processedAt")
    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    @JsonProperty("errorMessage")
    @Column(name = "error_message", nullable = false)
    private String errorMessage;
}
