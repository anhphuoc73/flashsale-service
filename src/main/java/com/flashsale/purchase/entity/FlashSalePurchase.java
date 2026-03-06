package com.flashsale.purchase.entity;

import com.flashsale.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "flashsale_purchases")
public class FlashSalePurchase extends BaseEntity {

    private String userId;

    private String flashSaleItemId;

    private String sessionId;

    private LocalDate purchaseDate;
}
