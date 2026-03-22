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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        
        UUID orderId = orderService.createDraftOrder(request, userEmail);
        return ResponseEntity.ok(Map.of(
                "orderId", orderId, 
                "message", "Заявка (черновик) успешно создана"
        ));
    }
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable UUID orderId,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден с ID: " + orderId));

        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("У вас нет доступа к этому заказу");
        }
        return ResponseEntity.ok(OrderResponse.fromOrder(order));
    }
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication authentication) {
        String email = authentication.getName();
        List<Order> userOrders = orderRepository.findByUser_Email(email);
        List<OrderResponse> responseList = userOrders.stream()
                .map(OrderResponse::fromOrder)
                .toList();

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
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        Order updatedOrder = orderService.confirmWagonSelection(orderId, wagonId, totalPrice, userEmail);
        return ResponseEntity.ok(OrderResponse.fromOrder(updatedOrder));
    }
    @GetMapping("/{orderId}/dto")
    public OrderDTO getOrderDTO(@PathVariable UUID orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}