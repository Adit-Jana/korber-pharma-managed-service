package com.korberpharma.order_service.korber_pharma_order_service.dto.request;

import lombok.NonNull;

public record InventoryReserveRequest(@NonNull Long productId, @NonNull Integer quantity) {
}
