package com.korberpharma.inventory_service.controller;

import com.korberpharma.inventory_service.dto.request.InventoryReserveRequest;
import com.korberpharma.inventory_service.dto.response.AvailabilityDetails;
import com.korberpharma.inventory_service.dto.response.InventoryResDto;
import com.korberpharma.inventory_service.dto.response.InventoryReserveResponse;
import com.korberpharma.inventory_service.service.impl.InventoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class InventoryControllerTest {

    @Mock
    private InventoryServiceImpl inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("getAllInventorByProductId")
    class GetAllInventorByProductId {

        @Test
        @DisplayName("Returns inventory details for a valid product ID")
        void returnsInventoryDetailsForValidProductId() {
            Long productId = 1L;
            InventoryResDto mockResponse = new InventoryResDto();
            when(inventoryService.getInventoryByProductId(productId)).thenReturn(mockResponse);

            ResponseEntity<InventoryResDto> response = inventoryController.getAllInventorByProductId(productId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockResponse, response.getBody());
            verify(inventoryService, times(1)).getInventoryByProductId(productId);
        }
    }

    @Nested
    @DisplayName("checkAvailability")
    class CheckAvailability {

        @Test
        @DisplayName("Returns availability details for valid product ID and quantity")
        void returnsAvailabilityDetailsForValidProductIdAndQuantity() {
            Long productId = 1L;
            Integer quantity = 10;
            AvailabilityDetails mockDetails = new AvailabilityDetails(10, 100, true);

            when(inventoryService.checkAvailability(productId, quantity)).thenReturn(mockDetails);

            ResponseEntity<AvailabilityDetails> response = inventoryController.checkAvailability(productId, quantity);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockDetails, response.getBody());
            verify(inventoryService, times(1)).checkAvailability(productId, quantity);
        }
    }

    @Nested
    @DisplayName("inventoryUpdate")
    class InventoryUpdate {

        @Test
        @DisplayName("Updates inventory and returns response for valid request")
        void updatesInventoryAndReturnsResponseForValidRequest() {
            InventoryReserveRequest request = new InventoryReserveRequest(1005L, 10);
            List<Long> batchIds = List.of(2001L, 2002L);

            InventoryReserveResponse mockResponse = new InventoryReserveResponse(1005L, batchIds);
            when(inventoryService.updateInventory(request)).thenReturn(mockResponse);

            ResponseEntity<InventoryReserveResponse> response = inventoryController.inventoryUpdate(request);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockResponse, response.getBody());
            verify(inventoryService, times(1)).updateInventory(request);
        }
    }
}
