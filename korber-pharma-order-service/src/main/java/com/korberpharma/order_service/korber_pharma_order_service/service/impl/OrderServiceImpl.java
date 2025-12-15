package com.korberpharma.order_service.korber_pharma_order_service.service.impl;


import com.korberpharma.order_service.korber_pharma_order_service.dto.request.InventoryReserveRequest;
import com.korberpharma.order_service.korber_pharma_order_service.dto.request.OrderRequest;
import com.korberpharma.order_service.korber_pharma_order_service.dto.response.AvailabilityDetails;
import com.korberpharma.order_service.korber_pharma_order_service.dto.response.InventoryReserveResponse;
import com.korberpharma.order_service.korber_pharma_order_service.dto.response.OrderResponse;
import com.korberpharma.order_service.korber_pharma_order_service.entity.Order;
import com.korberpharma.order_service.korber_pharma_order_service.entity.Product;
import com.korberpharma.order_service.korber_pharma_order_service.exception.ExternalServiceException;
import com.korberpharma.order_service.korber_pharma_order_service.exception.OrderServiceException;
import com.korberpharma.order_service.korber_pharma_order_service.exception.OutOfStockException;
import com.korberpharma.order_service.korber_pharma_order_service.repo.OrderRepo;
import com.korberpharma.order_service.korber_pharma_order_service.repo.ProductRepo;
import com.korberpharma.order_service.korber_pharma_order_service.service.OrderService;
import com.korberpharma.order_service.korber_pharma_order_service.status_code.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Objects;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final WebClient inventoryClient;
    private final OrderRepo orderRepo;
    private final ProductRepo productRepo;

    @Autowired
    public OrderServiceImpl(WebClient inventoryClient, OrderRepo orderRepo, ProductRepo productRepo) {
        this.inventoryClient = inventoryClient;
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
    }

    @Override
    public OrderResponse processOrder(OrderRequest orderRequest) {
        if (Objects.isNull(orderRequest)) {
            throw new OrderServiceException("Request payload required to place the order");
        } else {
            return createOrder(orderRequest);
        }
    }


    public OrderResponse createOrder(OrderRequest request) {
        // validate and process order request
        log.info("Processing order for product ID: {}", request.productId());
        Product product = productRepo.findById(request.productId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        boolean available = availabilityCheckForRequestedProduct(request);

        if (available) {
            InventoryReserveResponse reserveResponse = callInventoryToReserveStocks(request);
            return saveOrder(request, reserveResponse, product);
        } else {
            throw new OutOfStockException("Product is out of stock for product ID: " + request.productId());
        }
    }

    private OrderResponse saveOrder(OrderRequest request, InventoryReserveResponse reserveResponse, Product product) {
        try {
            log.info("Saving order for product ID: {}", request.productId());
            // saving order details
            Order order = new Order();
            order.setProductId(request.productId());
            order.setQuantity(request.quantity());
            order.setStatus(OrderStatus.PLACED);
            order.setCreatedAt(OffsetDateTime.now());
            order.setReservedFromBatchIds(!CollectionUtils.isEmpty(reserveResponse.batchIds()) ?
                    reserveResponse.batchIds() : null);

            order = orderRepo.save(order);
            log.info("Order saved successfully with Order ID: {}", order.getOrderId());
            return new OrderResponse(order.getOrderId(), order.getProductId(), product.getName(), order.getQuantity(),
                    order.getStatus().name(), order.getReservedFromBatchIds(), "Order placed. Inventory reserved."
            );
        } catch (Exception e) {
            log.error("Error saving order for product ID: {}: {}", request.productId(), e.getMessage());
            throw new OrderServiceException("Failed to save order for product ID: " + request.productId());
        }
    }

    private InventoryReserveResponse callInventoryToReserveStocks(OrderRequest request) {
        // calling inventory service to reserve stocks
        InventoryReserveResponse reserveResponse = inventoryClient.post()
                .uri("/update")
                .bodyValue(new InventoryReserveRequest(request.productId(), request.quantity()))
                .retrieve()
                .bodyToMono(InventoryReserveResponse.class)
                .block();
        if (Objects.nonNull(reserveResponse)) {
            log.info("Inventory reserved successfully for product ID: {}", request.productId());
            return reserveResponse;
        } else {
            throw new ExternalServiceException("Failed to reserve inventory for product ID: " + request.productId());
        }
    }

    private boolean availabilityCheckForRequestedProduct(OrderRequest request) {
        // checking availability from inventory service
        AvailabilityDetails available = inventoryClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/check")
                        .queryParam("productId", request.productId())
                        .queryParam("quantity", request.quantity())
                        .build())
                .retrieve()
                .bodyToMono(AvailabilityDetails.class)
                .block();

        if (Objects.nonNull(available) && Boolean.FALSE.equals(available.isAvailable())) {
            log.error("Insufficient stock for product ID: {}. Available quantity: {}", request.productId(), available.remainingCount());
            throw new OutOfStockException("Insufficient stock for product ID: " + request.productId() + ", Available quantity: " + available.remainingCount());
        }
        log.info("Sufficient stock available for product ID: {}", request.productId());
        return true;
    }

}
