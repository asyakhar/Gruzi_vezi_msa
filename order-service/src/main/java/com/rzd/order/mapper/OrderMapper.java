package com.rzd.order.mapper;

import com.rzd.common.dto.OrderDTO;
import com.rzd.order.model.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderDTO toDTO(Order order) {
        OrderDTO.OrderDTOBuilder builder = OrderDTO.builder()
                .id(order.getId())
                // Вместо order.getUser().getId() используем userId
                .userId(order.getUserId())
                // Вместо order.getUser().getCompanyName() используем companyName
                .companyName(order.getCompanyName())
                // Вместо order.getUser().getInn() используем userInn
                .inn(order.getUserInn())
                .departureStation(order.getDepartureStation())
                .destinationStation(order.getDestinationStation())
                // Проверяем на null для requestedWagonType
                .requestedWagonType(order.getRequestedWagonType() != null ? order.getRequestedWagonType().name() : null)
                // Проверяем на null для status
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .totalPrice(order.getTotalPrice())
                .carbonFootprintKg(order.getCarbonFootprintKg())
                .createdAt(order.getCreatedAt());

        // Вместо order.getWagon() используем wagonId и wagonNumber
        if (order.getWagonId() != null) {
            builder.wagonId(order.getWagonId())
                    .wagonNumber(order.getWagonNumber());
        }

        // Cargo остается без изменений (это entity внутри order-service)
        if (order.getCargo() != null) {
            builder.cargo(OrderDTO.CargoDTO.builder()
                    .cargoType(order.getCargo().getCargoType() != null ? order.getCargo().getCargoType().name() : null)
                    .weightKg(order.getCargo().getWeightKg())
                    .volumeM3(order.getCargo().getVolumeM3())
                    .packagingType(order.getCargo().getPackagingType() != null ? order.getCargo().getPackagingType().name() : null)
                    .build());
        }

        return builder.build();
    }
}