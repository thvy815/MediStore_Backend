package com.example.medistore.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.medistore.entity.order.DeliveryMethod;

import java.util.UUID;

@Repository
public interface DeliveryMethodRepository extends JpaRepository<DeliveryMethod, UUID> {
}