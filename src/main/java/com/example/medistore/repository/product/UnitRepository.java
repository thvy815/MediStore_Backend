package com.example.medistore.repository.product;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.medistore.entity.product.Unit;

public interface UnitRepository extends JpaRepository<Unit, UUID> {
}
