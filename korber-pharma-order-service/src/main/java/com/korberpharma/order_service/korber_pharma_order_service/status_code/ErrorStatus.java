package com.korberpharma.order_service.korber_pharma_order_service.status_code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus {
    PRODUCT_NOT_FOUND("ERR001", "Product not found"),
    OUT_OF_STOCK("ERR002", "Product is out of stock"),
    ORDER_SERVICE_ERROR("ERR003", "Order service error"),
    EXTERNAL_SERVICE_ERROR("ERR004", "External service error");

    private final String code;
    private final String message;
}
