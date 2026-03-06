package com.flashsale.purchase.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class PurchaseResponse {

    private String purchaseId;

    private String flashSaleItemId;

    private String sessionId;

    private String productId;

    private String productName;

    private BigDecimal originalPrice;

    private BigDecimal purchasePrice;

    private LocalDate purchaseDate;

    private LocalDateTime createdAt;
}