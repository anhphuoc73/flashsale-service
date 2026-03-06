package com.flashsale.flashsale.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class FlashSaleItemUserResponse {

    private String id;

    private String sessionId;
    
    private String sessionName;

    private String productId;

    private String productName;

    private BigDecimal originalPrice;

    private BigDecimal salePrice;

    private Integer remainingQuantity;

    private boolean soldOut;
}