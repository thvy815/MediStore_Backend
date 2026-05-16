package com.example.medistore.repository.order;

import com.example.medistore.entity.order.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VoucherRepository extends JpaRepository<Voucher, UUID> {
    Optional<Voucher> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(
            String code,
            UUID id);
}