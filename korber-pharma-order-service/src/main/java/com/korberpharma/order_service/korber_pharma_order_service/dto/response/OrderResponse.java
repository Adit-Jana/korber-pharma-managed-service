package com.korberpharma.order_service.korber_pharma_order_service.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

import java.util.List;

public record OrderResponse(@NonNull Long orderId,
                            @NonNull Long productId,
                            @NonNull @NotBlank String productName,
                            @NonNull Integer quantity,
                            @NonNull String status,
                            @NonNull List<Long> reservedFromBatchIds,
                            @NonNull @NotBlank String message) {
}
