package com.example.medistore.repository.order;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.medistore.entity.order.OrderVoucher;

public interface OrderVoucherRepository
        extends JpaRepository<OrderVoucher, UUID> {

    @Query("""
                SELECT COUNT(ov)
                FROM OrderVoucher ov
                WHERE ov.voucher.id = :voucherId
            """)
    int countVoucherUsed(UUID voucherId);

    @Query("""
                SELECT COUNT(ov)
                FROM OrderVoucher ov
                WHERE ov.voucher.id = :voucherId
                AND ov.order.user.id = :userId
            """)
    int countVoucherUsedByUser(
            UUID voucherId,
            UUID userId);

    @Query("""
                SELECT ov
                FROM OrderVoucher ov
                WHERE ov.voucher.id = :voucherId
            """)
    List<OrderVoucher> findHistoryByVoucherId(
            UUID voucherId);
}