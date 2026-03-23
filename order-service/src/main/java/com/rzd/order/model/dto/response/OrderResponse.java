package com.rzd.order.model.dto.response;

import com.rzd.common.enums.*;
import com.rzd.order.model.entity.Order;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public static OrderResponse fromOrder(Order order) {
        OrderResponseBuilder builder = OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .companyName(order.getCompanyName())
                .departureStation(order.getDepartureStation())
                .destinationStation(order.getDestinationStation())
                .requestedWagonType(order.getRequestedWagonType())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .carbonFootprintKg(order.getCarbonFootprintKg())
                .createdAt(order.getCreatedAt());

        if (order.getWagonId() != null) {
            builder.wagonId(order.getWagonId())
                    .wagonNumber(order.getWagonNumber());
        }

        if (order.getCargo() != null) {
            builder.cargo(CargoDto.builder()
                    .cargoType(order.getCargo().getCargoType())
                    .weightKg(order.getCargo().getWeightKg())
                    .volumeM3(order.getCargo().getVolumeM3())
                    .packagingType(order.getCargo().getPackagingType())
                    .build());
        }

        if (order.getServices() != null && !order.getServices().isEmpty()) {
            builder.services(order.getServices().stream()
                    .map(s -> ServiceDto.builder()
                            .serviceName(s.getServiceName())
                            .price(s.getPrice())
                            .build())
                    .collect(Collectors.toList()));
        }

        return builder.build();
    }
}