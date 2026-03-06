package com.flashsale.product.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductRequest {

    @Size(max = 255, message = "Product name must not exceed 255 characters")
    private String name;

    private String description;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @PositiveOrZero(message = "Stock cannot be negative")
    private Integer stock;

    private Boolean active;
}