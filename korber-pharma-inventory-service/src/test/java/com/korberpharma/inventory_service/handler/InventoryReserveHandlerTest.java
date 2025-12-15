package com.korberpharma.inventory_service.handler;

import com.korberpharma.inventory_service.dto.request.InventoryReserveRequest;
import com.korberpharma.inventory_service.dto.response.InventoryReserveResponse;
import com.korberpharma.inventory_service.entity.Batch;
import com.korberpharma.inventory_service.entity.Product;
import com.korberpharma.inventory_service.repo.BatchRepo;
import com.korberpharma.inventory_service.repo.ProductRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryReserveHandlerTest {

    @Mock
    private ProductRepo productRepo;

    @Mock
    private BatchRepo batchRepo;

    @InjectMocks
    private InventoryReserveHandler inventoryReserveHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("processInventoryUpdate")
    class ProcessInventoryUpdate {

        @Test
        @DisplayName("Reserves inventory successfully when sufficient stock is available")
        void reservesInventorySuccessfullyWhenSufficientStockIsAvailable() {
            InventoryReserveRequest request = new InventoryReserveRequest(1L, 10);
            Product product = new Product();
            product.setId(1L);
            product.setName("Product A");
            product.setDescription("A desc");


            Batch batch1 = new Batch();
            batch1.setId(1L);
            batch1.setProduct(product);
            batch1.setQuantityAvailable(5);
            batch1.setExpiryDate(LocalDate.of(2026, 3, 31));


            Batch batch2 = new Batch();
            batch2.setId(2L);
            batch2.setProduct(product);
            batch2.setQuantityAvailable(10);
            batch2.setExpiryDate(LocalDate.of(2026, 4, 24));


            when(productRepo.findById(1L)).thenReturn(Optional.of(product));
            when(batchRepo.findByProductId(1L)).thenReturn(Optional.of(List.of(batch1, batch2)));

            Optional<InventoryReserveResponse> response = inventoryReserveHandler.processInventoryUpdate(request);

            assertTrue(response.isPresent());
            assertEquals(1L, response.get().productId());
            assertEquals(List.of(1L, 2L), response.get().batchIds());
            assertEquals(0, batch1.getQuantityAvailable());
            assertEquals(5, batch2.getQuantityAvailable());
            verify(batchRepo, times(2)).save(any(Batch.class));
        }

        @Test
        @DisplayName("Throws exception when product is not found")
        void throwsExceptionWhenProductIsNotFound() {
            InventoryReserveRequest request = new InventoryReserveRequest(1L, 10);

            when(productRepo.findById(1L)).thenReturn(Optional.empty());

            ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                    () -> inventoryReserveHandler.processInventoryUpdate(request));

            assertEquals("404 NOT_FOUND \"Product not found\"", exception.getMessage());
            verify(batchRepo, never()).findByProductId(anyLong());
        }

        @Test
        @DisplayName("Throws exception when no batches are found for the product")
        void throwsExceptionWhenNoBatchesAreFoundForTheProduct() {
            InventoryReserveRequest request = new InventoryReserveRequest(1L, 10);
            Product product = new Product();
            product.setId(1L);
            product.setName("Product A");
            product.setDescription("A desc");

            when(productRepo.findById(1L)).thenReturn(Optional.of(product));
            when(batchRepo.findByProductId(1L)).thenReturn(Optional.empty());

            ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                    () -> inventoryReserveHandler.processInventoryUpdate(request));

            assertEquals("404 NOT_FOUND \"No batches found for product\"", exception.getMessage());
        }

        @Test
        @DisplayName("Throws exception when stock is insufficient to fulfill the request")
        void throwsExceptionWhenStockIsInsufficientToFulfillTheRequest() {
            InventoryReserveRequest request = new InventoryReserveRequest(1L, 15);
            Product product = new Product();
            product.setId(1L);
            product.setName("Product A");
            product.setDescription("A desc");

            Batch batch1 = new Batch();
            batch1.setId(1L);
            batch1.setProduct(product);
            batch1.setQuantityAvailable(5);
            batch1.setExpiryDate(LocalDate.of(2026, 3, 31));

            Batch batch2 = new Batch();
            batch2.setId(2L);
            batch2.setProduct(product);
            batch2.setQuantityAvailable(5);
            batch2.setExpiryDate(LocalDate.of(2026, 4, 24));

            when(productRepo.findById(1L)).thenReturn(Optional.of(product));
            when(batchRepo.findByProductId(1L)).thenReturn(Optional.of(List.of(batch1, batch2)));

            ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                    () -> inventoryReserveHandler.processInventoryUpdate(request));

            assertEquals("409 CONFLICT \"Not enough stock to fulfill the order, available quantity: 10\"", exception.getMessage());
        }

        @Test
        @DisplayName("Ignores expired batches when reserving inventory")
        void ignoresExpiredBatchesWhenReservingInventory() {
            InventoryReserveRequest request = new InventoryReserveRequest(1L, 5);

            Product product = new Product();
            product.setId(1L);
            product.setName("Product A");
            product.setDescription("A desc");

            Batch batch1 = new Batch();
            batch1.setId(1L);
            batch1.setProduct(product);
            batch1.setQuantityAvailable(10);
            batch1.setExpiryDate(LocalDate.of(2026, 3, 31));

            Batch batch2 = new Batch();
            batch2.setId(2L);
            batch2.setProduct(product);
            batch2.setQuantityAvailable(10);
            batch2.setExpiryDate(LocalDate.of(2026, 4, 24));

            when(productRepo.findById(1L)).thenReturn(Optional.of(product));
            when(batchRepo.findByProductId(1L)).thenReturn(Optional.of(List.of(batch1, batch2)));

            Optional<InventoryReserveResponse> response = inventoryReserveHandler.processInventoryUpdate(request);

            assertTrue(response.isPresent());
            assertEquals(1L, response.get().productId());
        }
    }
}
