package com.flashsale.session.dto.response;

import com.flashsale.session.enums.SessionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashSaleSessionResponse {

    private String id;

    private String name;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private SessionStatus status;
}
