package com.rzd.pricing.service;

import com.rzd.common.dto.OrderDTO;
import com.rzd.common.dto.WagonDTO;
import com.rzd.pricing.client.OrderServiceClient;
import com.rzd.pricing.client.WagonServiceClient;
import com.rzd.pricing.model.dto.request.PriceCalculationRequest;
import com.rzd.pricing.model.dto.response.PriceResponse;
import com.rzd.pricing.model.entity.StationDistance;
import com.rzd.pricing.model.entity.WagonTariff;
import com.rzd.pricing.repository.StationDistanceRepository;
import com.rzd.pricing.repository.WagonTariffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PricingService {

    private final WagonTariffRepository wagonTariffRepository;
    private final StationDistanceRepository distanceRepository;
    private final AdditionalServicesService additionalServicesService;


    private final OrderServiceClient orderServiceClient;
    private final WagonServiceClient wagonServiceClient;

    private static final BigDecimal CO2_FACTOR = new BigDecimal("0.02");

    @Transactional(readOnly = true)
    public PriceResponse calculateFullPrice(UUID orderId, UUID wagonId, Set<String> selectedServices) {
        log.info("Расчет полной стоимости для заказа: {}, вагон: {}", orderId, wagonId);


        OrderDTO order = orderServiceClient.getOrderDTO(orderId);
        WagonDTO wagon = wagonServiceClient.getWagonDTO(wagonId);


        int distance = getDistanceBetweenStations(
                order.getDepartureStation(),
                order.getDestinationStation()
        );
        log.info("Расстояние между станциями: {} км", distance);


        final String cargoTypeName;
        final Integer weightKg;
        if (order.getCargo() != null) {
            cargoTypeName = order.getCargo().getCargoType();
            weightKg = order.getCargo().getWeightKg();
        } else {
            cargoTypeName = "общий";
            weightKg = 0;
        }

        final String wagonTypeName = wagon.getWagonType();


        WagonTariff tariff = wagonTariffRepository.findByWagonTypeAndCargoType(
                wagonTypeName,
                cargoTypeName
        ).orElseThrow(() -> new RuntimeException(
                "Тариф не найден для вагона: " + wagonTypeName + " и груза: " + cargoTypeName));


        BigDecimal weightTons = new BigDecimal(weightKg)
                .divide(new BigDecimal(1000), 2, RoundingMode.HALF_UP);

        BigDecimal basePrice = weightTons
                .multiply(new BigDecimal(distance))
                .multiply(tariff.getBaseRatePerKm())
                .multiply(tariff.getCoefficient())
                .setScale(2, RoundingMode.HALF_UP);

        if (tariff.getMinPrice() != null && basePrice.compareTo(tariff.getMinPrice()) < 0) {
            basePrice = tariff.getMinPrice();
        }

        log.info("Базовая цена: {} руб", basePrice);


        List<PriceResponse.AdditionalServiceDto> allServices =
                additionalServicesService.getServicesWithSelection(
                        cargoTypeName,
                        order.getDepartureStation(),
                        order.getDestinationStation(),
                        weightKg,
                        basePrice,
                        distance,
                        selectedServices
                );


        BigDecimal servicesPrice = additionalServicesService.calculateServicesPrice(
                selectedServices,
                cargoTypeName,
                weightKg,
                basePrice,
                distance,
                order.getDepartureStation(),
                order.getDestinationStation()
        );

        log.info("Цена выбранных услуг: {} руб", servicesPrice);


        double carbonFootprint = calculateCarbonFootprint(weightKg, distance);


        BigDecimal cargoValue = additionalServicesService.estimateCargoValue(cargoTypeName, weightKg);


        return PriceResponse.builder()
                .basePrice(basePrice)
                .additionalServicesPrice(servicesPrice)
                .totalPrice(basePrice.add(servicesPrice))
                .distanceKm(distance)
                .carbonFootprintKg(carbonFootprint)
                .availableServices(allServices)
                .currency("RUB")
                .cargoEstimate(PriceResponse.CargoEstimate.builder()
                        .estimatedValue(cargoValue)
                        .weightTons(weightTons)
                        .cargoType(cargoTypeName)
                        .riskLevel(determineRiskLevel(cargoTypeName, weightKg))
                        .build())
                .build();
    }

    @Transactional(readOnly = true)
    public PriceResponse calculatePrice(PriceCalculationRequest request) {
        log.info("Расчет стоимости по запросу: груз={}, вагон={}, вес={}кг",
                request.getCargoType(), request.getWagonType(), request.getWeightKg());

        int distance = getDistanceBetweenStations(
                request.getDepartureStation(),
                request.getDestinationStation()
        );

        WagonTariff tariff = wagonTariffRepository.findByWagonTypeAndCargoType(
                request.getWagonType(),
                request.getCargoType()
        ).orElseThrow(() -> new RuntimeException("Тариф не найден"));

        BigDecimal weightTons = new BigDecimal(request.getWeightKg())
                .divide(new BigDecimal(1000), 2, RoundingMode.HALF_UP);

        BigDecimal basePrice = weightTons
                .multiply(new BigDecimal(distance))
                .multiply(tariff.getBaseRatePerKm())
                .multiply(tariff.getCoefficient())
                .setScale(2, RoundingMode.HALF_UP);

        if (tariff.getMinPrice() != null && basePrice.compareTo(tariff.getMinPrice()) < 0) {
            basePrice = tariff.getMinPrice();
        }

        List<PriceResponse.AdditionalServiceDto> allServices =
                additionalServicesService.getServicesWithSelection(
                        request.getCargoType(),
                        request.getDepartureStation(),
                        request.getDestinationStation(),
                        request.getWeightKg(),
                        basePrice,
                        distance,
                        request.getSelectedServices()
                );

        BigDecimal servicesPrice = additionalServicesService.calculateServicesPrice(
                request.getSelectedServices(),
                request.getCargoType(),
                request.getWeightKg(),
                basePrice,
                distance,
                request.getDepartureStation(),
                request.getDestinationStation()
        );

        double carbonFootprint = calculateCarbonFootprint(request.getWeightKg(), distance);
        BigDecimal cargoValue = additionalServicesService.estimateCargoValue(
                request.getCargoType(), request.getWeightKg());

        return PriceResponse.builder()
                .basePrice(basePrice)
                .additionalServicesPrice(servicesPrice)
                .totalPrice(basePrice.add(servicesPrice))
                .distanceKm(distance)
                .carbonFootprintKg(carbonFootprint)
                .availableServices(allServices)
                .currency("RUB")
                .cargoEstimate(PriceResponse.CargoEstimate.builder()
                        .estimatedValue(cargoValue)
                        .weightTons(weightTons)
                        .cargoType(request.getCargoType())
                        .riskLevel(determineRiskLevel(request.getCargoType(), request.getWeightKg()))
                        .build())
                .build();
    }

    /**
     * Расчет ориентировочной цены для заказа
     * @param orderId ID заказа
     * @param wagonType тип вагона
     * @return ориентировочная стоимость
     */
    @Transactional(readOnly = true)
    public PriceResponse calculateEstimatedPrice(UUID orderId, String wagonType) {
        log.info("Расчет ориентировочной цены для заказа: {}, тип вагона: {}", orderId, wagonType);


        OrderDTO order = orderServiceClient.getOrderDTO(orderId);


        int distance = getDistanceBetweenStations(
                order.getDepartureStation(),
                order.getDestinationStation()
        );
        log.info("Расстояние между станциями: {} км", distance);


        final String cargoTypeName;
        final Integer weightKg;
        if (order.getCargo() != null) {
            cargoTypeName = order.getCargo().getCargoType();
            weightKg = order.getCargo().getWeightKg();
        } else {
            cargoTypeName = "общий";
            weightKg = 0;
        }


        WagonTariff tariff;
        try {
            tariff = wagonTariffRepository.findByWagonTypeAndCargoType(
                    wagonType,
                    cargoTypeName
            ).orElseThrow(() -> new RuntimeException("Тариф не найден"));
        } catch (Exception e) {
            log.warn("Тариф не найден для вагона {} и груза {}, используем значения по умолчанию",
                    wagonType, cargoTypeName);
            tariff = new WagonTariff();
            tariff.setBaseRatePerKm(new BigDecimal("12.00"));
            tariff.setCoefficient(BigDecimal.ONE);
            tariff.setMinPrice(new BigDecimal("4000.00"));
        }


        BigDecimal weightTons = new BigDecimal(weightKg)
                .divide(new BigDecimal(1000), 2, RoundingMode.HALF_UP);

        BigDecimal estimatedPrice = weightTons
                .multiply(new BigDecimal(distance))
                .multiply(tariff.getBaseRatePerKm())
                .multiply(tariff.getCoefficient())
                .setScale(2, RoundingMode.HALF_UP);

        if (tariff.getMinPrice() != null && estimatedPrice.compareTo(tariff.getMinPrice()) < 0) {
            estimatedPrice = tariff.getMinPrice();
        }


        double carbonFootprint = calculateCarbonFootprint(weightKg, distance);


        BigDecimal cargoValue = additionalServicesService.estimateCargoValue(cargoTypeName, weightKg);

        log.info("Ориентировочная цена: {} руб", estimatedPrice);

        return PriceResponse.builder()
                .basePrice(estimatedPrice)
                .additionalServicesPrice(BigDecimal.ZERO)
                .totalPrice(estimatedPrice)
                .distanceKm(distance)
                .carbonFootprintKg(carbonFootprint)
                .currency("RUB")
                .cargoEstimate(PriceResponse.CargoEstimate.builder()
                        .estimatedValue(cargoValue)
                        .weightTons(weightTons)
                        .cargoType(cargoTypeName)
                        .riskLevel(determineRiskLevel(cargoTypeName, weightKg))
                        .build())
                .build();
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateSimplePrice(String wagonType, String cargoType, Integer weightKg, Integer distance) {
        log.info("Простой расчет цены: wagonType={}, cargoType={}, weight={}, distance={}",
                wagonType, cargoType, weightKg, distance);

        WagonTariff tariff = wagonTariffRepository.findByWagonTypeAndCargoType(wagonType, cargoType)
                .orElseGet(() -> wagonTariffRepository.findByWagonTypeAndCargoType(wagonType, "общий")
                        .orElse(null));

        if (tariff == null) {
            log.warn("Тариф не найден, возвращаем 0");
            return BigDecimal.ZERO;
        }

        BigDecimal weightTons = new BigDecimal(weightKg)
                .divide(new BigDecimal(1000), 2, RoundingMode.HALF_UP);

        BigDecimal price = weightTons
                .multiply(new BigDecimal(distance))
                .multiply(tariff.getBaseRatePerKm())
                .multiply(tariff.getCoefficient())
                .setScale(2, RoundingMode.HALF_UP);

        if (tariff.getMinPrice() != null && price.compareTo(tariff.getMinPrice()) < 0) {
            price = tariff.getMinPrice();
        }

        return price;
    }

    private String determineRiskLevel(String cargoType, Integer weightKg) {
        if (cargoType == null) return "Средний";

        String type = cargoType.toLowerCase();

        if (type.contains("хим") || type.contains("нефть") || type.contains("взрыв")) {
            return "Высокий";
        } else if (type.contains("электроник") || type.contains("оборуд")) {
            return "Средний";
        } else if (weightKg > 50000) {
            return "Повышенный";
        } else {
            return "Низкий";
        }
    }

    private int getDistanceBetweenStations(String from, String to) {
        return distanceRepository.findByFromStationAndToStation(from, to)
                .map(StationDistance::getDistanceKm)
                .orElseGet(() -> distanceRepository.findByFromStationAndToStation(to, from)
                        .map(StationDistance::getDistanceKm)
                        .orElse(1000));
    }

    private double calculateCarbonFootprint(Integer weightKg, Integer distanceKm) {
        BigDecimal weightTons = new BigDecimal(weightKg)
                .divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP);

        return weightTons
                .multiply(new BigDecimal(distanceKm))
                .multiply(CO2_FACTOR)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}