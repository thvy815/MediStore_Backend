package com.example.medistore.repository.product;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.medistore.entity.batch.ProductBatch;

@Repository
public interface ProductBatchRepository extends JpaRepository<ProductBatch, UUID> {
    List<ProductBatch> findByProductId(UUID productId);
}
