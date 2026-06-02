package com.example.medistore.service.batch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.medistore.dto.batch.BatchResponse;
import com.example.medistore.dto.batch.CreateBatchRequest;
import com.example.medistore.dto.batch.UpdateBatchRequest;
import com.example.medistore.entity.batch.Law;
import com.example.medistore.entity.batch.ProductBatch;
import com.example.medistore.entity.product.ProductUnit;
import com.example.medistore.repository.batch.BatchRepository;
import com.example.medistore.repository.batch.LawRepository;
import com.example.medistore.repository.batch.SupplierRepository;
import com.example.medistore.repository.product.ProductUnitRepository;
import com.example.medistore.service.batch.strategy.ExpiringSoonStrategy;
import com.example.medistore.service.batch.strategy.InStockStrategy;
import com.example.medistore.service.batch.strategy.LowStockStrategy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BatchService {

    private final BatchRepository batchRepository;
    private final SupplierRepository supplierRepository;
    private final ProductUnitRepository productUnitRepository;
    private final LawRepository lawRepository;
    private final BatchMapper batchMapper;
    private final ExpiringSoonStrategy expiringSoonStrategy;
    private final InStockStrategy inStockStrategy;
    private final LowStockStrategy lowStockStrategy;

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

        if (req.getImportPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Import price must be > 0");
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

        BigDecimal importPriceSmallest = req.getImportPrice().divide(
                BigDecimal.valueOf(unit.getConversionFactor()),
                2,
                RoundingMode.HALF_UP);

        ProductBatch batch = new ProductBatch();
        batch.setProduct(unit.getProduct());
        batch.setProductUnit(unit);
        batch.setSupplier(supplierRepository.getReferenceById(req.getSupplierId()));
        batch.setBatchNumber(req.getBatchNumber());
        batch.setManufactureDate(req.getManufactureDate());
        batch.setExpiryDate(req.getExpiryDate());
        batch.setQuantityImported(totalSmallestUnitQty);
        batch.setQuantityRemaining(totalSmallestUnitQty);
        batch.setImportPrice(importPriceSmallest);
        batch.setStatus("valid");
        batch.setLaw(
                req.getLawCode() == null
                        ? null
                        : lawRepository.findById(req.getLawCode())
                                .orElseThrow(() -> new RuntimeException("Law not found")));

        batchRepository.save(batch);
    }

    // Lấy thông tin lô hàng theo ID
    @Transactional(readOnly = true)
    public BatchResponse getBatchById(UUID id) {
        ProductBatch batch = batchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        return batchMapper.toResponse(batch);
    }

    // Lấy danh sách lô hàng với tùy chọn lọc
    @Transactional(readOnly = true)
    public List<BatchResponse> getAllBatches(UUID productId, String status) {

        List<ProductBatch> batches;

        if (productId != null && status != null) {
            batches = batchRepository.findByProductIdAndStatus(productId, status);
        } else if (productId != null) {
            batches = batchRepository.findByProductId(productId);
        } else if (status != null) {
            batches = batchRepository.findByStatus(status);
        } else {
            batches = batchRepository.findAll();
        }

        return batches
                .stream()
                .map(batchMapper::toResponse)
                .toList();
    }

    // Cập nhật thông tin lô hàng
    public void updateBatch(UUID id, UpdateBatchRequest req) {

        ProductBatch batch = batchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        // ❌ Không cho sửa batch đã recall
        if ("recalled".equals(batch.getStatus())) {
            throw new RuntimeException("Cannot update recalled batch");
        }

        // ========== METADATA ==========
        if (req.getBatchNumber() != null) {
            batch.setBatchNumber(req.getBatchNumber());
        }

        if (req.getManufactureDate() != null) {
            batch.setManufactureDate(req.getManufactureDate());
        }

        if (req.getExpiryDate() != null) {
            if (req.getExpiryDate().isBefore(LocalDate.now())) {
                throw new RuntimeException("Expiry date must be in the future");
            }
            batch.setExpiryDate(req.getExpiryDate());
        }

        if (req.getLawCode() != null) {
            Law law = lawRepository.findById(req.getLawCode())
                    .orElseThrow(() -> new RuntimeException("Law not found"));
            batch.setLaw(law);
        }
    }

    // Thu hồi lô hàng
    public void recallBatch(UUID id) {
        ProductBatch batch = batchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        batch.setStatus("recalled");
    }

    // Lấy danh sách batch sắp hết hạn
    @Transactional(readOnly = true)
    public List<BatchResponse> getExpiringSoonBatches() {

        return expiringSoonStrategy.execute();
    }

    // Lấy danh sách batch có tồn kho thấp
    @Transactional(readOnly = true)
    public List<BatchResponse> getLowStockBatches() {

        return lowStockStrategy.execute();
    }

    @Transactional(readOnly = true)
    public List<BatchResponse> getInStockBatches() {

        return inStockStrategy.execute();
    }

    /**
     * Lấy danh sách batch của 1 sản phẩm
     */
    @Transactional(readOnly = true)
    public List<BatchResponse> getBatchesByProduct(UUID productId) {

        return batchRepository.findByProductId(productId)
                .stream()
                .map(batchMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UUID> getProductIdsInStock() {
        return batchRepository.findProductIdsInStock(LocalDate.now());
    }

    /**
     * FEFO – phân bổ batch khi checkout @param requiredQuantity số lượng cần theo
     * đơn vị nhỏ nhất
     */
    public List<ProductBatch> allocateBatch(UUID productId, int requiredQuantity) {

        List<ProductBatch> batches = batchRepository.findByProductIdAndStatusAndExpiryDateAfterOrderByExpiryDateAsc(
                productId,
                "valid",
                LocalDate.now());

        int remaining = requiredQuantity;

        for (ProductBatch batch : batches) {

            if (remaining <= 0)
                break;

            int used = Math.min(batch.getQuantityRemaining(), remaining);
            batch.setQuantityRemaining(batch.getQuantityRemaining() - used);
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
}
