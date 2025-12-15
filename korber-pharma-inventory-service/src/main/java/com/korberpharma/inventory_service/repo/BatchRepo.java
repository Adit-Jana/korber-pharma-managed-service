package com.korberpharma.inventory_service.repo;

import com.korberpharma.inventory_service.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BatchRepo extends JpaRepository<Batch, Long> {
    Optional<List<Batch>> findByProductId(Long productId);
}
