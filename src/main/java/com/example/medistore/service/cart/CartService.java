package com.example.medistore.service.cart;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.medistore.dto.cart.AddToCartRequest;
import com.example.medistore.dto.cart.CartItemResponse;
import com.example.medistore.dto.cart.UpdateCartItemRequest;
import com.example.medistore.entity.cart.Cart;
import com.example.medistore.entity.cart.CartItem;
import com.example.medistore.entity.product.ProductUnit;
import com.example.medistore.repository.batch.BatchRepository;
import com.example.medistore.repository.cart.CartItemRepository;
import com.example.medistore.repository.cart.CartRepository;
import com.example.medistore.repository.product.ProductRepository;
import com.example.medistore.repository.product.ProductUnitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductUnitRepository productUnitRepository;
    private final BatchRepository batchRepository;

    public void addToCart(UUID userId, AddToCartRequest req) {

        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem item = cartItemRepository
            .findByCartIdAndProductIdAndProductUnitId(
                cart.getId(),
                req.getProductId(),
                req.getProductUnitId()
            )
            .orElse(null);
        
        // Tính tổng số lượng đã có trong cart
        int currentQuantityInCart = item != null ? item.getQuantity() * item.getProductUnit().getConversionFactor() : 0;

        // Chuyển quantity yêu cầu về smallest unit
        ProductUnit productUnit = productUnitRepository.getReferenceById(req.getProductUnitId());
        int requestedQtyInSmallestUnit = req.getQuantity() * productUnit.getConversionFactor();

        int availableStock = getAvailableStock(req.getProductId());

        if (requestedQtyInSmallestUnit + currentQuantityInCart > availableStock) {
            throw new RuntimeException("Cannot add to cart. Quantity exceeds available stock.");
        }

        if (item != null) {
            item.setQuantity(item.getQuantity() + req.getQuantity());
            return;
        }

        CartItem newItem = new CartItem();
        newItem.setCart(cart);
        newItem.setProduct(productRepository.getReferenceById(req.getProductId()));
        newItem.setProductUnit(productUnit);
        newItem.setQuantity(req.getQuantity());
        newItem.setUnitPrice(productUnit.getPrice());
        newItem.setSelected(false);

        cartItemRepository.save(newItem);
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse> getCart(UUID userId) {

        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Cart not found"));

        return cartItemRepository.findByCartId(cart.getId())
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    public void updateCartItem(UUID cartItemId, UpdateCartItemRequest req) {

        CartItem item = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // Update unit (nếu có)
        ProductUnit unit = item.getProductUnit();
        if (req.getProductUnitId() != null) {
            unit = productUnitRepository.findById(req.getProductUnitId())
                    .orElseThrow(() -> new RuntimeException("Product unit not found"));

            // Validate unit thuộc đúng product
            if (!unit.getProduct().getId().equals(item.getProduct().getId())) {
                throw new RuntimeException("Invalid product unit");
            }

            item.setProductUnit(unit);
            item.setUnitPrice(unit.getPrice()); // snapshot lại giá
        }

        // Update quantity
        if (req.getQuantity() <= 0) {
            cartItemRepository.delete(item);
            return;
        }

        int requestedQtyInSmallestUnit = req.getQuantity() * unit.getConversionFactor();

        int availableStock = getAvailableStock(item.getProduct().getId());

        if (requestedQtyInSmallestUnit > availableStock) {
            throw new RuntimeException("Cannot update cart. Quantity exceeds available stock.");
        }

        item.setQuantity(req.getQuantity());
        item.setSelected(req.isSelected());
    }

    public void removeCartItem(UUID cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    public void clearSelectedItems(UUID userId) {

        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Cart not found"));

        cartItemRepository.deleteByCartIdAndIsSelectedTrue(cart.getId());
    }

    private CartItemResponse mapToResponse(CartItem item) {

        CartItemResponse res = new CartItemResponse();
        res.setId(item.getId());
        res.setProductId(item.getProduct().getId());
        res.setProductName(item.getProduct().getName());
        res.setProductUnitId(item.getProductUnit().getId());
        res.setUnitName(item.getProductUnit().getUnit().getName());
        res.setQuantity(item.getQuantity());
        res.setUnitPrice(item.getUnitPrice());
        res.setSelected(item.isSelected());

        return res;
    }

    private int getAvailableStock(UUID productId) {
        // Lấy tất cả batch hợp lệ của sản phẩm còn tồn kho
        return batchRepository
            .findByProductIdAndStatusAndExpiryDateAfter(productId, "valid", LocalDate.now())
            .stream()
            .mapToInt(batch -> batch.getQuantityRemaining()) // quantityRemaining đã là smallest unit
            .sum();
    }
}
