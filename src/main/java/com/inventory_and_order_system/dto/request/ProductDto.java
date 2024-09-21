package com.inventory_and_order_system.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ProductDto {
    @NotEmpty(message = "name is required.")
    private String name;

    @NotEmpty(message = "description is required.")
    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "The price must be greater than 0")
    private Double price;
}
