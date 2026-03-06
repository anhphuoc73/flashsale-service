package com.flashsale.product.entity;

import com.flashsale.common.entity.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "products")
public class Product extends BaseEntity {

    @Indexed(unique = true)
    private String name;

    private String description;

    private BigDecimal price;

    private int stock;

    @Indexed
    private boolean active;
}
