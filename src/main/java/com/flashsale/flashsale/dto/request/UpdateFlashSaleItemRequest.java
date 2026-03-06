package com.flashsale.flashsale.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateFlashSaleItemRequest {
    @DecimalMin(value = "0.01")
    private BigDecimal salePrice;

    @Positive
    private Integer quantityLimit;
}
