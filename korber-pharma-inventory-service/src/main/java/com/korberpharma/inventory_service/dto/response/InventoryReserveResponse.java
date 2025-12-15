package com.korberpharma.inventory_service.dto.response;

import lombok.NonNull;

import java.util.List;


public record InventoryReserveResponse(@NonNull Long productId,
                                       @NonNull List<Long> batchIds) {
}
