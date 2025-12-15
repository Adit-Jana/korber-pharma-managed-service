package com.korberpharma.inventory_service.dto.response;

public record AvailabilityDetails(Integer askedCount, Integer remainingCount, Boolean isAvailable) {
}
