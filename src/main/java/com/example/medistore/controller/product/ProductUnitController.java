package com.example.medistore.controller.product;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import com.example.medistore.dto.product.ProductUnitResponse;
import com.example.medistore.service.product.ProductUnitService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductUnitController {

    private final ProductUnitService productUnitService;

    /**
     * ADMIN / KHO
     */
    @GetMapping("/admin/products/{productId}/units")
    public List<ProductUnitResponse> getAllUnitsByProduct(
            @PathVariable UUID productId) {
        return productUnitService.getUnitsByProduct(productId);
    }

    /**
     * CUSTOMER
     */
    @GetMapping("/products/{productId}/units")
    public List<ProductUnitResponse> getActiveUnitsByProduct(
            @PathVariable UUID productId) {
        return productUnitService.getActiveUnitsByProduct(productId);
    }
}
