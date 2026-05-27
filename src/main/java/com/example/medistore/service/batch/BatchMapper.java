package com.example.medistore.service.batch;

import org.springframework.stereotype.Component;

import com.example.medistore.dto.batch.BatchResponse;
import com.example.medistore.entity.batch.ProductBatch;
import com.example.medistore.entity.product.ProductUnit;
import com.example.medistore.repository.product.ProductUnitRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BatchMapper {

    private final ProductUnitRepository productUnitRepository;

    public BatchResponse toResponse(ProductBatch batch) {

        BatchResponse res = new BatchResponse();

        // Batch
        res.setId(batch.getId());
        res.setBatchNumber(batch.getBatchNumber());
        res.setManufactureDate(batch.getManufactureDate());
        res.setExpiryDate(batch.getExpiryDate());
        res.setQuantityImported(batch.getQuantityImported());
        res.setQuantityRemaining(batch.getQuantityRemaining());
        res.setImportPrice(batch.getImportPrice());
        res.setStatus(batch.getStatus());
        res.setCreatedAt(batch.getCreatedAt());
        res.setUpdatedAt(batch.getUpdatedAt());

        // Product
        res.setProductId(batch.getProduct().getId());
        res.setProductName(batch.getProduct().getName());

        // Supplier
        res.setSupplierId(batch.getSupplier().getId());
        res.setSupplierName(batch.getSupplier().getName());

        // Law
        if (batch.getLaw() != null) {
            res.setLawCode(batch.getLaw().getCode());
            res.setLawTitle(batch.getLaw().getTitle());
        }

        // Smallest unit
        ProductUnit smallestUnit = productUnitRepository
                .findByProductIdAndConversionFactor(
                        batch.getProduct().getId(),
                        1)
                .orElseThrow(() -> new RuntimeException(
                        "Smallest unit not found"));

        res.setSmallestUnitName(
                smallestUnit
                        .getUnit()
                        .getName());

        return res;
    }
}