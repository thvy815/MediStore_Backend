package com.example.medistore.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.medistore.entity.order.PaymentMethod;

import java.util.UUID;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, UUID> {
}