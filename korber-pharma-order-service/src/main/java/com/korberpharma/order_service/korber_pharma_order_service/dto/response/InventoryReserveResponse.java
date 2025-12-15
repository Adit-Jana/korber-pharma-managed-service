package com.korberpharma.order_service.korber_pharma_order_service.dto.response;

import jakarta.validation.constraints.NotEmpty;
import lombok.NonNull;

import java.util.List;


public record InventoryReserveResponse(@NonNull Long productId,
                                       @NonNull @NotEmpty List<Long> batchIds) {
}
