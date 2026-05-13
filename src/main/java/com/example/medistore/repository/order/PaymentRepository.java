package com.example.medistore.repository.order;

import com.example.medistore.entity.order.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository
        extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByTransactionRef(String transactionRef);

    List<Payment> findByOrderId(UUID orderId);
}