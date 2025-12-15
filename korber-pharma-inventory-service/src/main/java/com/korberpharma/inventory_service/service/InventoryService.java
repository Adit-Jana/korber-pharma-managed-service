package com.korberpharma.inventory_service.service;

import com.korberpharma.inventory_service.entity.Batch;
import com.korberpharma.inventory_service.entity.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InventoryService {

    List<Batch> getBatchListByProductId(Long productId);

    Product getProductById(Long productId);


}
