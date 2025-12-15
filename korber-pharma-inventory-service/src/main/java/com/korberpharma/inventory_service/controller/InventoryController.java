package com.korberpharma.inventory_service.controller;


import com.korberpharma.inventory_service.dto.request.InventoryReserveRequest;
import com.korberpharma.inventory_service.dto.response.AvailabilityDetails;
import com.korberpharma.inventory_service.dto.response.InventoryResDto;
import com.korberpharma.inventory_service.dto.response.InventoryReserveResponse;
import com.korberpharma.inventory_service.service.impl.InventoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1/korber-pharma/inventory")
@RestController
public class InventoryController {

    private final InventoryServiceImpl inventoryService;

    @Autowired
    public InventoryController(InventoryServiceImpl inventoryService) {
        this.inventoryService = inventoryService;
    }


    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResDto> getAllInventorByProductId(
            @PathVariable @Validated Long productId) {
        InventoryResDto inventoryResponse = inventoryService.getInventoryByProductId(productId);
        return new ResponseEntity<>(inventoryResponse, HttpStatus.OK);
    }

    @GetMapping("/check")
    public ResponseEntity<AvailabilityDetails> checkAvailability(
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        AvailabilityDetails available = inventoryService.checkAvailability(productId, quantity);
        return ResponseEntity.ok(available);
    }

    @PostMapping("/update")
    public ResponseEntity<InventoryReserveResponse> inventoryUpdate(@RequestBody InventoryReserveRequest inventoryReserveRequest) {
        InventoryReserveResponse response = inventoryService.updateInventory(inventoryReserveRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
