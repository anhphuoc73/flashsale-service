package com.flashsale.purchase.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseRequest {

    @NotBlank(message = "Flash sale item id is required")
    private String flashSaleItemId;
}