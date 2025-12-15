package com.korberpharma.order_service.korber_pharma_order_service.exception;

public class OutOfStockException extends RuntimeException {

    public OutOfStockException(String message) {
        super(message);
    }
}
