package com.rzd.order.repository;

import com.rzd.common.enums.OrderStatus;
import com.rzd.order.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    // Поиск по email пользователя (вместо user_id)
    List<Order> findByUserEmail(String userEmail);

    List<Order> findByUserEmailAndStatus(String userEmail, OrderStatus status);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByWagonId(UUID wagonId);

    @Modifying
    @Query("UPDATE Order o SET o.status = :status WHERE o.id = :orderId")
    int updateStatus(@Param("orderId") UUID orderId, @Param("status") OrderStatus status);

    @Modifying
    @Query("UPDATE Order o SET o.totalPrice = :price WHERE o.id = :orderId")
    int updatePrice(@Param("orderId") UUID orderId, @Param("price") BigDecimal price);

    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> getOrderStatistics();
}