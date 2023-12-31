package com.courrier.consumerdelivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdjustementModified {
    private UUID adjustmentId;
    private UUID deliveryId;
    private UUID courierId;
    private Instant createdTimestamp;
    private String value;
}
