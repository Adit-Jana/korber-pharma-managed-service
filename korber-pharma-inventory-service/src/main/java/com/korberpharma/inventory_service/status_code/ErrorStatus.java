package com.korberpharma.inventory_service.status_code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus {
    PRODUCT_NOT_FOUND("ERR001", "Product not found");

    private final String code;
    private final String message;
}
