package com.korberpharma.order_service.korber_pharma_order_service.dto.request;

import jakarta.validation.constraints.Min;
import lombok.NonNull;

public record OrderRequest(@NonNull Long productId,
                           @NonNull @Min(value = 1, message = "order quantity should be greater then 0 (zero)") Integer quantity) {
}
