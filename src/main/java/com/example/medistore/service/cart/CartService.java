package com.example.medistore.service.cart;

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

        if (item != null) {
            item.setQuantity(item.getQuantity() + req.getQuantity());
            return;
        }

        ProductUnit productUnit =
            productUnitRepository.getReferenceById(req.getProductUnitId());

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

        // Update quantity
        if (req.getQuantity() <= 0) {
            cartItemRepository.delete(item);
            return;
        }

        // Update unit (nếu có)
        if (req.getProductUnitId() != null) {

            ProductUnit newUnit =
                productUnitRepository.findById(req.getProductUnitId())
                    .orElseThrow(() -> new RuntimeException("Product unit not found"));

            // Validate unit thuộc đúng product
            if (!newUnit.getProduct().getId().equals(item.getProduct().getId())) {
                throw new RuntimeException("Invalid product unit");
            }

            item.setProductUnit(newUnit);
            item.setUnitPrice(newUnit.getPrice()); // snapshot lại giá
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
}
