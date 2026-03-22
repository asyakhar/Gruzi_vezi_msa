package com.rzd.common.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class OrderDTO {
    private UUID id;
    private UUID userId;
    private String companyName;
    private String inn;
    private String departureStation;
    private String destinationStation;
    private String requestedWagonType;
    private UUID wagonId;
    private String wagonNumber;
    private String status;
    private BigDecimal totalPrice;
    private BigDecimal carbonFootprintKg;
    private OffsetDateTime createdAt;
    private CargoDTO cargo;

    @Data
    @Builder
    public static class CargoDTO {
        private String cargoType;
        private Integer weightKg;
        private Integer volumeM3;
        private String packagingType;
    }
}