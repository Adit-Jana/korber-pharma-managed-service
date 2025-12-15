package com.korberpharma.inventory_service.repo;

import com.korberpharma.inventory_service.entity.Product;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;

@Registered
public interface ProductRepo extends JpaRepository<Product, Long> {
}
