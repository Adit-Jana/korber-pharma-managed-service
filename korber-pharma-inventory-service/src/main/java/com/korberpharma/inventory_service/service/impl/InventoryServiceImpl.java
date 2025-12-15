package com.korberpharma.inventory_service.service.impl;

import com.korberpharma.inventory_service.dto.request.InventoryReserveRequest;
import com.korberpharma.inventory_service.dto.response.AvailabilityDetails;
import com.korberpharma.inventory_service.dto.response.BatchDetails;
import com.korberpharma.inventory_service.dto.response.InventoryResDto;
import com.korberpharma.inventory_service.dto.response.InventoryReserveResponse;
import com.korberpharma.inventory_service.entity.Batch;
import com.korberpharma.inventory_service.entity.Product;
import com.korberpharma.inventory_service.exception.ProductNotFound;
import com.korberpharma.inventory_service.handler.InventoryReserveHandler;
import com.korberpharma.inventory_service.repo.BatchRepo;
import com.korberpharma.inventory_service.repo.ProductRepo;
import com.korberpharma.inventory_service.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class InventoryServiceImpl implements InventoryService {

    private final BatchRepo batchRepo;
    private final ProductRepo productRepo;
    private final InventoryReserveHandler inventoryReserveHandler;

    @Autowired
    public InventoryServiceImpl(BatchRepo batchRepo, ProductRepo productRepo, InventoryReserveHandler inventoryReserveHandler) {
        this.batchRepo = batchRepo;
        this.productRepo = productRepo;
        this.inventoryReserveHandler = inventoryReserveHandler;
    }

    public InventoryResDto getInventoryByProductId(Long productId) {
        log.info("Fetching inventory for productId: {}", productId);
        InventoryResDto inventoryResDto = new InventoryResDto();
        Product product = getProductById(productId);
        if (Objects.nonNull(product)) {
            inventoryResDto.setProductId(product.getId());
            inventoryResDto.setProductName(product.getName());
            List<Batch> batchList = getBatchListByProductId(productId);
            if (!batchList.isEmpty()) {
                log.info("Mapping {} batches to response DTO for productId {}", batchList.size(), productId);
                List<BatchDetails> productBatches = batchList.stream()
                        .map(batch -> {
                            BatchDetails productBatch = new BatchDetails();
                            productBatch.setBatchId(batch.getId().intValue());
                            productBatch.setQuantity(batch.getQuantityAvailable());
                            productBatch.setExpiryDate(batch.getExpiryDate().toString());
                            return productBatch;
                        })
                        .sorted(Comparator.comparing(BatchDetails::getExpiryDate))
                        .toList();
                inventoryResDto.setBatches(productBatches);
            } else {
                inventoryResDto.setBatches(Collections.emptyList());
                log.warn("No available batches found for productId {}", productId);
            }
        } else {
            log.warn("Product not found for productId: {}", productId);
            throw new ProductNotFound("Product not found for productId: " + productId);
        }
        return inventoryResDto;
    }


    @Override
    public List<Batch> getBatchListByProductId(Long productId) {
        List<Batch> batchList = List.of();
        if (Objects.nonNull(productId)) {
            log.info("Valid productId received: {}", productId);
            Optional<List<Batch>> batches = batchRepo.findByProductId(productId);
            if (batches.isPresent()) {
                log.info("Total no of {} Batches found for productId {}", batches.get().size(), productId);
                batchList = batches.get().stream()
                        .filter(batch -> batch.getQuantityAvailable() > 0)
                        .toList();
            } else {
                log.warn("No batches found for productId {}", productId);
            }
        }
        return batchList;
    }

    @Override
    public Product getProductById(Long productId) {
        if (Objects.nonNull(productId)) {
            log.info("Fetching product details for productId: {}", productId);
            Optional<Product> productOpt = productRepo.findById(productId);
            if (productOpt.isPresent()) {
                log.info("Product found in database: {}", productOpt.get().getName());
                return productOpt.get();
            } else {
                log.warn("No product found for productId: {}", productId);
            }
        }
        return null;
    }


    public AvailabilityDetails checkAvailability(Long productId, Integer quantity) {
        // Check availability
        int totalAvailable = 0;
        int toReserve = quantity;

        Optional<List<Batch>> batches = batchRepo.findByProductId(productId);
        if (batches.isPresent()) {
            totalAvailable = batches.get().stream()
                    .filter(b -> !b.getExpiryDate().isBefore(LocalDate.now()))
                    .mapToInt(Batch::getQuantityAvailable)
                    .sum();
        }

        if (quantity > totalAvailable) {
            toReserve = totalAvailable; // Adjust quantity to available stock
        }
        AvailabilityDetails availabilityDetails = new AvailabilityDetails(quantity, toReserve, totalAvailable >= quantity);
        log.info("Availability check for productId {}: requested {}, available {}", productId, quantity, totalAvailable);
        return availabilityDetails;
    }

    public InventoryReserveResponse updateInventory(InventoryReserveRequest inventoryReserveRequest) {
        // update inventory
        return inventoryReserveHandler.processInventoryUpdate(inventoryReserveRequest)
                .map(resp -> { log.info("Inventory updated successfully for productId: {}",
                        resp.productId()); return resp; })
                .orElseGet(() -> { log.error("Failed to update inventory for productId: {}",
                        inventoryReserveRequest.productId()); throw new ResponseStatusException(HttpStatus.CONFLICT); });
    }

}
