package com.example.medistore.controller.cart;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.medistore.dto.cart.AddToCartRequest;
import com.example.medistore.dto.cart.CartItemResponse;
import com.example.medistore.dto.cart.UpdateCartItemRequest;
import com.example.medistore.service.cart.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<Void> addToCart(
        @RequestParam UUID userId,
        @RequestBody AddToCartRequest request
    ) {
        cartService.addToCart(userId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCart(
        @RequestParam UUID userId
    ) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PutMapping("/item/{id}")
    public ResponseEntity<Void> updateCartItem(
        @PathVariable UUID id,
        @RequestBody UpdateCartItemRequest request
    ) {
        cartService.updateCartItem(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/item/{id}")
    public ResponseEntity<Void> removeCartItem(@PathVariable UUID id) {
        cartService.removeCartItem(id);
        return ResponseEntity.ok().build();
    }
}
