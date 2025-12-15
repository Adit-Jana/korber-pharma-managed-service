package com.korberpharma.inventory_service.dto.request;

import lombok.NonNull;

public record InventoryReserveRequest(@NonNull Long productId, @NonNull Integer quantity) {
}
