package com.example.medistore.repository.batch;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.medistore.entity.batch.ProductBatch;

@Repository
public interface BatchRepository extends JpaRepository<ProductBatch, UUID> {
    List<ProductBatch> findByProductId(UUID productId);
    boolean existsByProductId(UUID productId);
    boolean existsByProductUnitId(UUID productUnitId);
    List<ProductBatch> findByStatus(String status);
    List<ProductBatch> findByProductIdAndStatus(UUID productId, String status);

    // Lấy các lô hàng còn hạn sử dụng cho một sản phẩm cụ thể, sắp xếp theo ngày hết hạn tăng dần
    List<ProductBatch> findByProductIdAndStatusAndExpiryDateAfterOrderByExpiryDateAsc(
        UUID productId,
        String status,
        LocalDate today
    );

    List<ProductBatch> findByStatusAndQuantityRemainingLessThan(
        String status,
        Integer quantityRemaining
    );

    @Query("""
        SELECT DISTINCT b.product.id
        FROM ProductBatch b
        WHERE b.status = 'valid'
          AND b.quantityRemaining > 0
          AND b.expiryDate >= :today
    """)
    List<UUID> findProductIdsInStock(@Param("today") LocalDate today);

    @Query("""
        SELECT b
        FROM ProductBatch b
        WHERE b.product.id = :productId
        AND b.status = 'valid'
        AND b.quantityRemaining > 0
        AND b.expiryDate >= :today
        ORDER BY b.expiryDate ASC
    """)
    List<ProductBatch> findAvailableBatches(@Param("productId") UUID productId, @Param("today") LocalDate today);

    @Query("""
        SELECT b FROM ProductBatch b
        WHERE b.status = 'valid'
        AND b.expiryDate BETWEEN :now AND :warningDate
    """)
    List<ProductBatch> findExpiringSoon(
        @Param("now") LocalDate now,
        @Param("warningDate") LocalDate warningDate
    );

    @Query("""
        SELECT b FROM ProductBatch b
        WHERE b.status = 'valid'
        AND b.quantityRemaining > 0
        AND b.expiryDate >= :today
    """)
    List<ProductBatch> findBatchesInStock(LocalDate today);

}
