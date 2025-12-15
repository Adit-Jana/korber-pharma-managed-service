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
import com.korberpharma.order_service.korber_pharma_order_service.status_code.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderServiceImplTest {

    @Mock
    private WebClient inventoryClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private OrderRepo orderRepo;

    @Mock
    private ProductRepo productRepo;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        product = new Product();
        product.setId(1002L);
        product.setName("Smartphone");
    }

    private void mockAvailability(AvailabilityDetails details) {
        when(inventoryClient.get()).thenReturn(requestHeadersUriSpec);

        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AvailabilityDetails.class)).thenReturn(Mono.just(details));
    }


    private void mockReserve(InventoryReserveResponse response) {
        when(inventoryClient.post()).thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri(eq("/update"))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any(InventoryReserveRequest.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(InventoryReserveResponse.class)).thenReturn(Mono.just(response));
    }

    @Test
    void testProcessOrder_NullRequestThrowsException() {
        assertThrows(OrderServiceException.class, () -> orderService.processOrder(null));
    }

    @Test
    void testCreateOrder_ProductNotFoundThrowsException() {
        when(productRepo.findById(1002L)).thenReturn(Optional.empty());
        OrderRequest request = new OrderRequest(1002L, 3);
        assertThrows(ResponseStatusException.class, () -> orderService.createOrder(request));
    }

    @Test
    void testCreateOrder_OutOfStockThrowsException() {
        when(productRepo.findById(1002L)).thenReturn(Optional.of(product));
        AvailabilityDetails unavailable = mock(AvailabilityDetails.class);
        when(unavailable.isAvailable()).thenReturn(false);
        when(unavailable.remainingCount()).thenReturn(1);
        mockAvailability(unavailable);

        OrderRequest request = new OrderRequest(1002L, 3);
        assertThrows(OutOfStockException.class, () -> orderService.createOrder(request));
    }

    @Test
    void testCreateOrder_ReserveFailsThrowsException() {
        when(productRepo.findById(1002L)).thenReturn(Optional.of(product));
        AvailabilityDetails available = mock(AvailabilityDetails.class);
        when(available.isAvailable()).thenReturn(true);
        when(available.remainingCount()).thenReturn(10);
        mockAvailability(available);

        when(inventoryClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(eq("/update"))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(InventoryReserveResponse.class)).thenReturn(Mono.empty());

        OrderRequest request = new OrderRequest(1002L, 3);
        assertThrows(ExternalServiceException.class, () -> orderService.createOrder(request));
    }

    @Test
    void testCreateOrder_SaveFailsThrowsException() {
        when(productRepo.findById(1002L)).thenReturn(Optional.of(product));
        AvailabilityDetails available = mock(AvailabilityDetails.class);
        when(available.isAvailable()).thenReturn(true);
        when(available.remainingCount()).thenReturn(10);
        mockAvailability(available);

        InventoryReserveResponse reserveResponse = new InventoryReserveResponse(1L, List.of(3L));
        mockReserve(reserveResponse);

        when(orderRepo.save(any(Order.class))).thenThrow(new RuntimeException("DB error"));

        OrderRequest request = new OrderRequest(1002L, 3);
        assertThrows(OrderServiceException.class, () -> orderService.createOrder(request));
    }

    @Test
    void testCreateOrder_Success() {
        when(productRepo.findById(1002L)).thenReturn(Optional.of(product));
        AvailabilityDetails available = mock(AvailabilityDetails.class);
        when(available.isAvailable()).thenReturn(true);
        when(available.remainingCount()).thenReturn(10);
        mockAvailability(available);

        InventoryReserveResponse reserveResponse = new InventoryReserveResponse(1L, List.of(3L));
        mockReserve(reserveResponse);

        Order savedOrder = new Order();
        savedOrder.setOrderId(5012L);
        savedOrder.setProductId(1002L);
        savedOrder.setQuantity(3);
        savedOrder.setStatus(OrderStatus.PLACED);
        savedOrder.setReservedFromBatchIds(List.of(3L));

        when(orderRepo.save(any(Order.class))).thenReturn(savedOrder);

        OrderRequest request = new OrderRequest(1002L, 3);
        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(5012L, response.orderId());
        assertEquals(1002L, response.productId());
        assertEquals("Smartphone", response.productName());
        assertEquals(3, response.quantity());
        assertEquals("PLACED", response.status());
        assertEquals(List.of(3L), response.reservedFromBatchIds());
        assertEquals("Order placed. Inventory reserved.", response.message());
    }
}
