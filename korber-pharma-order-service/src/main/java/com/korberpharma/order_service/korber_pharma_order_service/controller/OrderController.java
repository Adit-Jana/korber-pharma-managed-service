package com.korberpharma.order_service.korber_pharma_order_service.controller;

import com.korberpharma.order_service.korber_pharma_order_service.dto.request.OrderRequest;
import com.korberpharma.order_service.korber_pharma_order_service.dto.response.OrderResponse;
import com.korberpharma.order_service.korber_pharma_order_service.service.impl.OrderServiceImpl;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/korber-pharma")
@RestController
public class OrderController {

    private final OrderServiceImpl orderService;

    @Autowired
    public OrderController(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place-order")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody @NonNull OrderRequest request) {
        OrderResponse response = orderService.processOrder(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
