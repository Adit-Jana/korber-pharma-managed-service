package com.korberpharma.order_service.korber_pharma_order_service.service;


import com.korberpharma.order_service.korber_pharma_order_service.dto.request.OrderRequest;
import com.korberpharma.order_service.korber_pharma_order_service.dto.response.OrderResponse;

public interface OrderService {

    OrderResponse processOrder(OrderRequest orderRequest);
}
