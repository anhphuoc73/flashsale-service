package com.flashsale.session.dto.request;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateFlashSaleSessionRequest {

    private String name;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Boolean active;
}
