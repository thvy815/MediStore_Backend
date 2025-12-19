package com.example.medistore.repository.cart;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.medistore.entity.cart.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    Optional<CartItem> findByCartIdAndProductIdAndProductUnitId(
        UUID cartId,
        UUID productId,
        UUID productUnitId
    );

    List<CartItem> findByCartId(UUID cartId);

    void deleteByCartIdAndIsSelectedTrue(UUID cartId);
}

