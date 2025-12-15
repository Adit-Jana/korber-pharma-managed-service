package com.korberpharma.order_service.korber_pharma_order_service.entity;


import com.korberpharma.order_service.korber_pharma_order_service.status_code.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @ElementCollection
    @CollectionTable(name = "order_reserved_batches", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "batch_id")
    private List<Long> reservedFromBatchIds = new ArrayList<>();

    @Column(nullable = false)
    private OffsetDateTime createdAt;

}
