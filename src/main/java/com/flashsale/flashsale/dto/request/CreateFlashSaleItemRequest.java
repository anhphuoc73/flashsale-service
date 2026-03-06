package com.flashsale.flashsale.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
public class CreateFlashSaleItemRequest {

    @NotBlank(message = "SessionId is required")
    private String sessionId;

    @NotBlank(message = "productId is required")
    private String productId;

    @NotNull(message = "Sale price is required")
    @DecimalMin(value = "0.01", message = "Sale price must be greater than 0")
    private BigDecimal salePrice;

    @NotNull(message = "Quantity limit is required")
    @Positive(message = "Quantity limit must be greater than 0")
    private Integer quantityLimit;
}
