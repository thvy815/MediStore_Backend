package com.example.medistore.service.batch;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.medistore.dto.batch.BatchResponse;
import com.example.medistore.dto.batch.CreateBatchRequest;
import com.example.medistore.entity.batch.ProductBatch;
import com.example.medistore.entity.product.ProductUnit;
import com.example.medistore.repository.batch.BatchRepository;
import com.example.medistore.repository.batch.SupplierRepository;
import com.example.medistore.repository.product.ProductUnitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BatchService {

    private final BatchRepository batchRepository;
    private final SupplierRepository supplierRepository;
    private final ProductUnitRepository productUnitRepository;

    /**
     * Nhập kho (tạo batch mới)
     */
    public void createBatch(CreateBatchRequest req) {
        ProductUnit unit = productUnitRepository.findById(req.getProductUnitId())
            .orElseThrow(() -> new RuntimeException("Product unit not found"));

        if (!unit.getProduct().getId().equals(req.getProductId())) {
            throw new RuntimeException("Product unit does not belong to product");
        }

        if (req.getQuantity() <= 0) {
            throw new RuntimeException("Quantity must be > 0");
        }

        if (unit.getConversionFactor() <= 0) {
            throw new RuntimeException("Invalid conversion factor");
        }

        if (req.getExpiryDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Expiry date must be in the future");
        }

        if (!unit.getIsActive()) {
            throw new RuntimeException("Product unit is inactive");
        }

        int totalSmallestUnitQty = req.getQuantity() * unit.getConversionFactor();

        ProductBatch batch = new ProductBatch();
        batch.setProduct(unit.getProduct());
        batch.setProductUnit(unit);
        batch.setSupplier(supplierRepository.getReferenceById(req.getSupplierId()));
        batch.setBatchNumber(req.getBatchNumber());
        batch.setManufactureDate(req.getManufactureDate());
        batch.setExpiryDate(req.getExpiryDate());
        batch.setQuantity(totalSmallestUnitQty); // lưu đơn vị nhỏ nhất
        batch.setStatus("valid");

        batchRepository.save(batch);
    }

    /**
     * Lấy danh sách batch của 1 sản phẩm
     */
    @Transactional(readOnly = true)
    public List<BatchResponse> getBatchesByProduct(UUID productId) {

        return batchRepository.findByProductId(productId)
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<UUID> getProductIdsInStock() {
        return batchRepository.findProductIdsInStock(LocalDate.now());
    }

    /**
     * FEFO – phân bổ batch khi checkout @param requiredQuantity số lượng cần theo đơn vị nhỏ nhất
     */
    public List<ProductBatch> allocateBatch(UUID productId, int requiredQuantity) {

        List<ProductBatch> batches =
            batchRepository.findByProductIdAndStatusAndExpiryDateAfterOrderByExpiryDateAsc(
                productId,
                "valid",
                LocalDate.now()
            );

        int remaining = requiredQuantity;

        for (ProductBatch batch : batches) {

            if (remaining <= 0) break;

            int used = Math.min(batch.getQuantity(), remaining);
            batch.setQuantity(batch.getQuantity() - used);
            remaining -= used;
        }

        if (remaining > 0) {
            throw new RuntimeException("Not enough stock");
        }

        return batches;
    }

    /**
     * Đánh dấu batch hết hạn (cron job)
     */
    public void markExpiredBatches() {

        List<ProductBatch> all = batchRepository.findAll();

        for (ProductBatch batch : all) {
            if (batch.getExpiryDate().isBefore(LocalDate.now())
                && batch.getStatus().equals("valid")) {

                batch.setStatus("expired");
            }
        }
    }

    private BatchResponse mapToResponse(ProductBatch batch) {

        BatchResponse res = new BatchResponse();
        res.setId(batch.getId());
        res.setBatchNumber(batch.getBatchNumber());
        res.setManufactureDate(batch.getManufactureDate());
        res.setExpiryDate(batch.getExpiryDate());
        res.setQuantity(batch.getQuantity());
        res.setStatus(batch.getStatus());

        return res;
    }
}
