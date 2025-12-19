package com.example.medistore.repository.product;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.medistore.entity.product.ProductUnit;

@Repository
public interface ProductUnitRepository extends JpaRepository<ProductUnit, UUID> {
    List<ProductUnit> findByProductId(UUID productId);
    List<ProductUnit> findByProductIdAndIsActiveTrue(UUID productId);
    boolean existsByProductIdAndConversionFactor(UUID productId, Integer conversionFactor);
}
