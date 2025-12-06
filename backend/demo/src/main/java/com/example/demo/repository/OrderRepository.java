package com.example.demo.repository;

import com.example.demo.model.OrderTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderTable, Long> {
    Optional<OrderTable> findByShopifyOrderId(Long shopifyOrderId);
}
