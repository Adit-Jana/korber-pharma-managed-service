package com.korberpharma.inventory_service.handler;

import com.korberpharma.inventory_service.dto.request.InventoryReserveRequest;
import com.korberpharma.inventory_service.dto.response.InventoryReserveResponse;
import com.korberpharma.inventory_service.entity.Batch;
import com.korberpharma.inventory_service.entity.Product;
import com.korberpharma.inventory_service.repo.BatchRepo;
import com.korberpharma.inventory_service.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class InventoryReserveHandler {

    private final ProductRepo productRepo;
    private final BatchRepo batchRepo;

    @Autowired
    public InventoryReserveHandler(ProductRepo productRepo, BatchRepo batchRepo) {
        this.productRepo = productRepo;
        this.batchRepo = batchRepo;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Optional<InventoryReserveResponse> processInventoryUpdate(InventoryReserveRequest req) {
        // process inventory reservation
        Product product = productRepo.findById(req.productId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        List<Long> reservedBatchIds = new ArrayList<>();
        int toReserve = req.quantity();

        // batches for the product
        List<Batch> batches = batchRepo.findByProductId(req.productId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No batches found for product"));
        List<Batch> eligible = batches.stream()
                .filter(b -> !b.getExpiryDate().isBefore(LocalDate.now()))
                .filter(b -> b.getQuantityAvailable() > 0)
                .sorted(Comparator
                        .comparing(Batch::getExpiryDate)
                        .thenComparing(Batch::getId))
                .toList();

        int remaining = toReserve;
        for (Batch b : eligible) {
            if (remaining == 0) break;
            int take = Math.min(b.getQuantityAvailable(), remaining);
            if (take > 0) {
                b.setQuantityAvailable(b.getQuantityAvailable() - take);
                batchRepo.save(b);
                reservedBatchIds.add(b.getId());
                remaining -= take;
            }
        }

        if (remaining > 0) { // pending quantity
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Not enough stock to fulfill the order, available quantity: " + (toReserve - remaining));
        }
        return Optional.of(new InventoryReserveResponse(product.getId(), reservedBatchIds));
    }
}
