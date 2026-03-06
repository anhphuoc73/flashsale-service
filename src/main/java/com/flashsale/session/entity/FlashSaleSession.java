package com.flashsale.session.entity;

import com.flashsale.common.entity.BaseEntity;
import lombok.*;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "flash_sale_session")
@CompoundIndex(
    name = "active_time_idx",
    def = "{'active': 1, 'startTime': 1, 'endTime': 1}"
)
public class FlashSaleSession extends BaseEntity {

    private String name;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private boolean active;
}
