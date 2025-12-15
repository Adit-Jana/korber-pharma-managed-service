package com.korberpharma.inventory_service.service.impl;

import com.korberpharma.inventory_service.dto.response.AvailabilityDetails;
import com.korberpharma.inventory_service.dto.response.BatchDetails;
import com.korberpharma.inventory_service.dto.response.InventoryResDto;
import com.korberpharma.inventory_service.entity.Batch;
import com.korberpharma.inventory_service.entity.Product;
import com.korberpharma.inventory_service.exception.ProductNotFound;
import com.korberpharma.inventory_service.handler.InventoryReserveHandler;
import com.korberpharma.inventory_service.repo.BatchRepo;
import com.korberpharma.inventory_service.repo.ProductRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {


    @Mock
    private BatchRepo batchRepo;

    @Mock
    private ProductRepo productRepo;
    @Mock
    private InventoryReserveHandler inventoryReserveHandler;

    private InventoryServiceImpl inventoryServiceImpl;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        inventoryServiceImpl = new InventoryServiceImpl(batchRepo, productRepo, inventoryReserveHandler);
    }


    @Test
    @DisplayName("Returns availability details when sufficient stock is available")
    void returnsAvailabilityDetailsWhenSufficientStockIsAvailable() {
        Long productId = 1L;
        Integer quantity = 5;

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

        when(batchRepo.findByProductId(productId)).thenReturn(Optional.of(List.of(batch1, batch2)));

        AvailabilityDetails response = inventoryServiceImpl.checkAvailability(productId, quantity);

        assertEquals(quantity, response.askedCount());
        assertTrue(response.isAvailable());
    }

    @Test
    @DisplayName("Returns partial availability details when stock is insufficient")
    void returnsPartialAvailabilityDetailsWhenStockIsInsufficient() {
        Long productId = 1L;
        Integer quantity = 10;

        Product product = new Product();
        product.setId(1L);
        product.setName("Product A");
        product.setDescription("A desc");

        Batch batch1 = new Batch();
        batch1.setId(1L);
        batch1.setProduct(product);
        batch1.setQuantityAvailable(3);
        batch1.setExpiryDate(LocalDate.of(2026, 3, 31));


        Batch batch2 = new Batch();
        batch2.setId(2L);
        batch2.setProduct(product);
        batch2.setQuantityAvailable(2);
        batch2.setExpiryDate(LocalDate.of(2026, 4, 24));

        when(batchRepo.findByProductId(productId)).thenReturn(Optional.of(List.of(batch1, batch2)));

        AvailabilityDetails response = inventoryServiceImpl.checkAvailability(productId, quantity);

        assertEquals(quantity, response.askedCount());
        assertEquals(5, response.remainingCount());
        assertFalse(response.isAvailable());
        verify(batchRepo, times(1)).findByProductId(productId);
    }

    @Test
    @DisplayName("Returns zero availability when no batches are found")
    void returnsZeroAvailabilityWhenNoBatchesAreFound() {
        Long productId = 1L;
        Integer quantity = 5;

        AvailabilityDetails response = inventoryServiceImpl.checkAvailability(productId, quantity);

        assertEquals(quantity, response.askedCount());
        assertEquals(0, response.remainingCount());
        assertFalse(response.isAvailable());

    }


    @Test
    @DisplayName("Returns inventory details for valid product ID with available batches")
    void returnsInventoryDetailsForValidProductIdWithAvailableBatches() {
        Long productId = 1L;
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


        BatchDetails b1 = new BatchDetails();
        b1.setBatchId(1);
        b1.setQuantity(5);
        b1.setExpiryDate(batch1.getExpiryDate().toString());

        BatchDetails b2 = new BatchDetails();
        b2.setBatchId(2);
        b2.setQuantity(10);
        b2.setExpiryDate(batch2.getExpiryDate().toString());


        InventoryResDto expectedResponse = new InventoryResDto();
        expectedResponse.setProductId(productId);
        expectedResponse.setProductName("Product A");
        expectedResponse.setBatches(List.of(b1, b2));

        when(productRepo.findById(productId)).thenReturn(Optional.of(product));
        when(batchRepo.findByProductId(productId)).thenReturn(Optional.of(List.of(batch1, batch2)));

        InventoryResDto response = inventoryServiceImpl.getInventoryByProductId(productId);

        assertEquals(expectedResponse, response);
        verify(productRepo, times(1)).findById(productId);
        verify(batchRepo, times(1)).findByProductId(productId);
    }

    @Test
    @DisplayName("Throws ProductNotFound exception for invalid product ID")
    void throwsProductNotFoundExceptionForInvalidProductId() {
        Long productId = 1L;

        when(productRepo.findById(productId)).thenReturn(Optional.empty());

        ProductNotFound exception = assertThrows(ProductNotFound.class,
                () -> inventoryServiceImpl.getInventoryByProductId(productId));

        assertEquals("Product not found for productId: " + productId, exception.getMessage());
        verify(productRepo, times(1)).findById(productId);
        verify(batchRepo, never()).findByProductId(anyLong());
    }

    @Test
    @DisplayName("Returns inventory details with empty batches when no batches are available")
    void returnsInventoryDetailsWithEmptyBatchesWhenNoBatchesAreAvailable() {
        Long productId = 1L;
        Product product = new Product();
        product.setId(1L);
        product.setName("Product A");
        product.setDescription("A desc");

        when(productRepo.findById(productId)).thenReturn(Optional.of(product));
        when(batchRepo.findByProductId(productId)).thenReturn(Optional.empty());

        InventoryResDto response = inventoryServiceImpl.getInventoryByProductId(productId);

        assertEquals(productId, response.getProductId());
        assertEquals("Product A", response.getProductName());
        assertTrue(response.getBatches().isEmpty());
        verify(productRepo, times(1)).findById(productId);
        verify(batchRepo, times(1)).findByProductId(productId);
    }

}
