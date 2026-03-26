package com.rzd.order.controller;

import com.rzd.common.dto.OrderDTO;
import com.rzd.order.model.dto.request.CreateOrderRequest;
import com.rzd.order.model.dto.response.OrderResponse;
import com.rzd.order.model.entity.Order;
import com.rzd.order.repository.OrderRepository;
import com.rzd.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @PostMapping
    public ResponseEntity<?> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @RequestHeader("X-User-Email") String userEmail // Читаем email от Gateway
    ) {
        UUID orderId = orderService.createDraftOrder(request, userEmail);
        return ResponseEntity.ok(Map.of(
                "orderId", orderId,
                "message", "Заявка (черновик) успешно создана"
        ));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable UUID orderId,
            @RequestHeader("X-User-Email") String userEmail
    ) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден с ID: " + orderId));

        if (!order.getUserEmail().equals(userEmail)) {
            throw new RuntimeException("У вас нет доступа к этому заказу");
        }
        return ResponseEntity.ok(OrderResponse.fromOrder(order));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders(@RequestHeader("X-User-Email") String userEmail) {
        List<Order> userOrders = orderRepository.findByUserEmail(userEmail);
        List<OrderResponse> responseList = userOrders.stream()
                .map(OrderResponse::fromOrder)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{orderId}/contract")
    public ResponseEntity<byte[]> downloadContract(@PathVariable UUID orderId) {
        byte[] pdfContent = orderService.generateOrderContract(orderId);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=\"contract_" + orderId + ".pdf\"")
                .body(pdfContent);
    }

    @PostMapping("/{orderId}/confirm-wagon")
    public ResponseEntity<OrderResponse> confirmWagon(
            @PathVariable UUID orderId,
            @RequestParam UUID wagonId,
            @RequestParam BigDecimal totalPrice,
            @RequestHeader("X-User-Email") String userEmail
    ) {
        Order updatedOrder = orderService.confirmWagonSelection(orderId, wagonId, totalPrice, userEmail);
        return ResponseEntity.ok(OrderResponse.fromOrder(updatedOrder));
    }

    @GetMapping("/{orderId}/dto")
    public OrderDTO getOrderDTO(@PathVariable UUID orderId) {
        return orderService.getOrderDTO(orderId);
    }
}