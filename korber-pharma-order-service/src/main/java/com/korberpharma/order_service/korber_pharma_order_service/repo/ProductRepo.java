package com.korberpharma.order_service.korber_pharma_order_service.repo;

import com.korberpharma.order_service.korber_pharma_order_service.entity.Product;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Registered
public interface ProductRepo extends JpaRepository<Product, Long> {

    Optional<Product> findById(Long id);
}
