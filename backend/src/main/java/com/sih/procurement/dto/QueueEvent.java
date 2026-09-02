package com.sih.procurement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// This is the single object that flows: Service -> Redis -> Listener -> WebSocket -> browser
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueueEvent {
    private Long centreId;
    private Integer currentlyProcessingToken;
    private long waitingCount;
    private String message; // e.g. "TOKEN_COMPLETED", "TOKEN_PROCESSING", "TOKEN_CREATED"
}
