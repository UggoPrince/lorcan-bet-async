package com.inventory_and_order_system.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.inventory_and_order_system.validation.NotEmptyIfPresent;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateProductDto {
    @NotEmptyIfPresent(message = "name is required.")
    private String name;

    @NotEmptyIfPresent(message = "description is required.")
    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "The price must be greater than 0")
    private Double price;
}
