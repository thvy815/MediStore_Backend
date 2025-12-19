package com.example.medistore.repository.batch;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.medistore.entity.batch.Supplier;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
}
