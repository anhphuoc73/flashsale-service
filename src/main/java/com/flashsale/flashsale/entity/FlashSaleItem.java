package com.flashsale.flashsale.entity;

import com.flashsale.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Getter
@Setter
@Document(collection = "flash_sales_items")
@CompoundIndex(name = "session_product_idx",
        def = "{'sessionId':1,'productId':1}",
        unique = true)
public class FlashSaleItem extends BaseEntity{

    @Indexed
    private String sessionId;

    @Indexed
    private String productId;

    private String productName;

    private BigDecimal originalPrice;

    private String productDescription;

    private BigDecimal salePrice;

    private int quantityLimit;

    private int soldQuantity = 0;

    @Indexed
    private boolean active = true;
}
