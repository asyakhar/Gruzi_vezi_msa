package com.rzd.order.mapper;

import com.rzd.common.dto.OrderDTO;
import com.rzd.order.model.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderDTO toDTO(Order order) {
        OrderDTO.OrderDTOBuilder builder = OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .companyName(order.getUser().getCompanyName())
                .inn(order.getUser().getInn())
                .departureStation(order.getDepartureStation())
                .destinationStation(order.getDestinationStation())
                .requestedWagonType(order.getRequestedWagonType().name())
                .status(order.getStatus().name())
                .totalPrice(order.getTotalPrice())
                .carbonFootprintKg(order.getCarbonFootprintKg())
                .createdAt(order.getCreatedAt());

        if (order.getWagon() != null) {
            builder.wagonId(order.getWagon().getId())
                    .wagonNumber(order.getWagon().getWagonNumber());
        }

        if (order.getCargo() != null) {
            builder.cargo(OrderDTO.CargoDTO.builder()
                    .cargoType(order.getCargo().getCargoType().name())
                    .weightKg(order.getCargo().getWeightKg())
                    .volumeM3(order.getCargo().getVolumeM3())
                    .packagingType(order.getCargo().getPackagingType().name())
                    .build());
        }

        return builder.build();
    }
}