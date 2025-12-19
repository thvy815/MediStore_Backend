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

    // Lấy các lô hàng còn hạn sử dụng cho một sản phẩm cụ thể, sắp xếp theo ngày hết hạn tăng dần
    List<ProductBatch> findByProductIdAndStatusAndExpiryDateAfterOrderByExpiryDateAsc(
        UUID productId,
        String status,
        LocalDate today
    );

    @Query("""
        SELECT DISTINCT b.product.id
        FROM ProductBatch b
        WHERE b.status = 'valid'
          AND b.quantity > 0
          AND b.expiryDate >= :today
    """)
    List<UUID> findProductIdsInStock(@Param("today") LocalDate today);
}
