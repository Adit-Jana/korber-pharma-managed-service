package com.korberpharma.inventory_service.error;

import com.korberpharma.inventory_service.exception.ProductNotFound;
import com.korberpharma.inventory_service.status_code.ErrorStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class OmsErrorResponseHandler {

    @ExceptionHandler(ProductNotFound.class)
    public ResponseEntity<ErrorResponse> handleError(ProductNotFound ex) {
        LocalDateTime localDateTime = LocalDateTime.now();

        return new ResponseEntity<>(ErrorResponse.builder()
                .errorMessage(ex.getMessage())
                .errorCode(ErrorStatus.PRODUCT_NOT_FOUND.getCode())
                .errorDesc(ErrorStatus.PRODUCT_NOT_FOUND.getMessage())
                .timestamp(localDateTime.toString())
                .build(), HttpStatus.OK);
    }

}
