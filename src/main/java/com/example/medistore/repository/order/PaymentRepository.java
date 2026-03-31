package com.example.medistore.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.medistore.entity.order.Payment;

import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}