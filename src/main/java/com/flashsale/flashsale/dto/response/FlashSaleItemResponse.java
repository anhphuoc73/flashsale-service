package com.flashsale.flashsale.dto.response;

import com.flashsale.flashsale.enums.FlashSaleItemStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class FlashSaleItemResponse {

    private String id;

    private String sessionId;

    private String productId;

    private String productName;

    private BigDecimal originalPrice;

    private String productDescription;

    private BigDecimal salePrice;

    private int quantityLimit;

    private int soldQuantity;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean active;

    private FlashSaleItemStatus status;


}
