package com.rzd.common.dto.response;

import com.rzd.common.enums.CargoType;
import com.rzd.common.enums.OrderStatus;
import com.rzd.common.enums.PackagingType;
import com.rzd.common.enums.ServiceName;
import com.rzd.common.enums.WagonType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderResponse {
    private UUID id;
    private UUID userId;
    private String companyName;
    private String departureStation;
    private String destinationStation;


    private WagonType requestedWagonType;

    private UUID wagonId;
    private String wagonNumber;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private BigDecimal carbonFootprintKg;
    private OffsetDateTime createdAt;
    private CargoDto cargo;
    private List<ServiceDto> services;

    @Data
    @Builder
    public static class CargoDto {
        private CargoType cargoType;
        private Integer weightKg;
        private Integer volumeM3;
        private PackagingType packagingType;
    }

    @Data
    @Builder
    public static class ServiceDto {
        private ServiceName serviceName;
        private BigDecimal price;
    }
}