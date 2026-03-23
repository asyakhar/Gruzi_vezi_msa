package com.rzd.order.service;

import com.rzd.common.dto.OrderDTO;
import com.rzd.common.dto.UserDTO;
import com.rzd.common.enums.OrderStatus;
import com.rzd.order.client.DocumentServiceClient;
import com.rzd.order.client.InventoryServiceClient;
import com.rzd.order.client.PricingServiceClient;
import com.rzd.order.client.UserServiceClient;
import com.rzd.order.mapper.OrderMapper;
import com.rzd.order.model.dto.request.CreateOrderRequest;
import com.rzd.order.model.entity.Cargo;
import com.rzd.order.model.entity.Order;
import com.rzd.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderValidator orderValidator;
    private final OrderMapper orderMapper;


    private final UserServiceClient userServiceClient;
    private final InventoryServiceClient inventoryServiceClient;
    private final PricingServiceClient pricingServiceClient;
    private final DocumentServiceClient documentServiceClient;

    @Transactional
    public UUID createDraftOrder(CreateOrderRequest request, String userEmail) {
        log.info("Создание черновика заказа для пользователя: {}", userEmail);

        orderValidator.validate(request);
        UserDTO user = userServiceClient.getUserByEmail(userEmail);

        Order order = new Order();
        order.setUserEmail(user.getEmail());
        order.setCompanyName(user.getCompanyName());
        order.setDepartureStation(request.getDepartureStation());
        order.setDestinationStation(request.getDestinationStation());
        order.setRequestedWagonType(request.getRequestedWagonType());
        order.setStatus(OrderStatus.черновик);

        Cargo cargo = new Cargo();
        cargo.setCargoType(request.getCargo().getCargoType());
        cargo.setWeightKg(request.getCargo().getWeightKg());
        cargo.setVolumeM3(request.getCargo().getVolumeM3());
        cargo.setPackagingType(request.getCargo().getPackagingType());

        order.setCargo(cargo);
        cargo.setOrder(order);

        Order savedOrder = orderRepository.save(order);
        log.info("Черновик заказа создан: {}", savedOrder.getId());

        return savedOrder.getId();
    }

    @Transactional
    public void updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        log.info("Обновление статуса заказа {} на: {}", orderId, newStatus);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден с ID: " + orderId));

        order.setStatus(newStatus);
        orderRepository.save(order);

        if (newStatus == OrderStatus.оплачен) {
            try {
                documentServiceClient.generateContract(orderId);
                log.info("Договор для заказа {} отправлен на генерацию", orderId);
            } catch (Exception e) {
                log.error("Ошибка при генерации договора: {}", e.getMessage());
            }
        }

        log.info("Статус заказа {} обновлен на: {}", orderId, newStatus);
    }

    @Transactional
    public Order confirmWagonSelection(UUID orderId, UUID wagonId, BigDecimal totalPrice, String userEmail) {
        log.info("Подтверждение выбора вагона: заказ={}, вагон={}, цена={}", orderId, wagonId, totalPrice);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (!order.getUserEmail().equals(userEmail)) {
            throw new RuntimeException("Нет доступа к заказу");
        }

        boolean reserved = inventoryServiceClient.reserveWagon(wagonId, orderId, 30);
        if (!reserved) {
            throw new RuntimeException("Не удалось зарезервировать вагон");
        }

        order.setWagonId(wagonId);
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.ожидает_оплаты);

        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден с ID: " + orderId));
    }


    @Transactional(readOnly = true)
    public byte[] generateOrderContract(UUID orderId) {
        log.info("Генерация договора для заказа: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        try {

            return documentServiceClient.generateContract(orderId);
        } catch (Exception e) {
            log.error("Ошибка при создании договора для заказа {}", orderId, e);
            throw new RuntimeException("Ошибка генерации PDF: " + e.getMessage());
        }
    }


    @Transactional(readOnly = true)
    public OrderDTO getOrderDTO(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден с ID: " + orderId));
        return orderMapper.toDTO(order);
    }
}