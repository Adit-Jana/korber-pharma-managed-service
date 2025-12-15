package com.korberpharma.order_service.korber_pharma_order_service.error;


import com.korberpharma.order_service.korber_pharma_order_service.exception.ExternalServiceException;
import com.korberpharma.order_service.korber_pharma_order_service.exception.OrderServiceException;
import com.korberpharma.order_service.korber_pharma_order_service.exception.OutOfStockException;
import com.korberpharma.order_service.korber_pharma_order_service.exception.ProductNotFound;
import com.korberpharma.order_service.korber_pharma_order_service.status_code.ErrorStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class OmsErrorResponseHandler {


    @ExceptionHandler(ProductNotFound.class)
    public ResponseEntity<ErrorResponse> productNotFoundResponse(ProductNotFound ex) {
        LocalDateTime localDateTime = LocalDateTime.now();

        return new ResponseEntity<>(ErrorResponse.builder()
                .errorMessage(ex.getMessage())
                .errorCode(ErrorStatus.PRODUCT_NOT_FOUND.getCode())
                .errorDesc(ErrorStatus.PRODUCT_NOT_FOUND.getMessage())
                .timestamp(localDateTime.toString())
                .build(), HttpStatus.OK);
    }


    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<ErrorResponse> productOutOfStockResponse(OutOfStockException ex) {
        LocalDateTime localDateTime = LocalDateTime.now();

        return new ResponseEntity<>(ErrorResponse.builder()
                .errorMessage(ex.getMessage())
                .errorCode(ErrorStatus.OUT_OF_STOCK.getCode())
                .errorDesc(ErrorStatus.OUT_OF_STOCK.getMessage())
                .timestamp(localDateTime.toString())
                .build(), HttpStatus.OK);
    }

    @ExceptionHandler(OrderServiceException.class)
    public ResponseEntity<ErrorResponse> orderServiceExceptionResponse(OrderServiceException ex) {
        LocalDateTime localDateTime = LocalDateTime.now();

        return new ResponseEntity<>(ErrorResponse.builder()
                .errorMessage(ex.getMessage())
                .errorCode(ErrorStatus.ORDER_SERVICE_ERROR.getCode())
                .errorDesc(ErrorStatus.ORDER_SERVICE_ERROR.getMessage())
                .timestamp(localDateTime.toString())
                .build(), HttpStatus.OK);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> orderServiceExceptionResponse(ExternalServiceException ex) {
        LocalDateTime localDateTime = LocalDateTime.now();

        return new ResponseEntity<>(ErrorResponse.builder()
                .errorMessage(ex.getMessage())
                .errorCode(ErrorStatus.EXTERNAL_SERVICE_ERROR.getCode())
                .errorDesc(ErrorStatus.EXTERNAL_SERVICE_ERROR.getMessage())
                .timestamp(localDateTime.toString())
                .build(), HttpStatus.OK);
    }

}
