package com.example.medistore.repository.product;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.medistore.entity.product.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCodeContainingIgnoreCase(String code);
    boolean existsByCode(String code);
    List<Product> findByIsActiveTrue();
}
