package com.korberpharma.order_service.korber_pharma_order_service.controller;

import com.korberpharma.order_service.korber_pharma_order_service.dto.request.OrderRequest;
import com.korberpharma.order_service.korber_pharma_order_service.dto.response.OrderResponse;
import com.korberpharma.order_service.korber_pharma_order_service.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

class OrderControllerTest {

    @Mock
    private OrderServiceImpl orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void shouldReturnOkStatusAndResponseWhenOrderIsSuccessfullyProcessed() {
        OrderRequest request = new OrderRequest(1L, 5);

        OrderResponse expectedResponse = new OrderResponse(1001L, 1L, "Test Product",
                5, "SUCCESS", List.of(10L, 11L), "Order placed successfully");
        when(orderService.processOrder(request)).thenReturn(expectedResponse);

        ResponseEntity<OrderResponse> response = orderController.placeOrder(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }


    @Test
    void shouldReturnOkStatusWithNullBodyWhenServiceReturnsNull() {
        OrderRequest request = new OrderRequest(2L, 3);
        when(orderService.processOrder(request)).thenReturn(null);

        ResponseEntity<OrderResponse> response = orderController.placeOrder(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void shouldPropagateRuntimeExceptionThrownByService() {
        OrderRequest request = new OrderRequest(3L, 2);
        when(orderService.processOrder(request)).thenThrow(new RuntimeException("processing failed"));

        Assertions.assertThrows(RuntimeException.class, () -> orderController.placeOrder(request));
    }

    @Test
    void shouldThrowNullPointerWhenRequestIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> orderController.placeOrder(null));
    }


}