package com.korberpharma.order_service.korber_pharma_order_service.repo;

import com.korberpharma.order_service.korber_pharma_order_service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

}
