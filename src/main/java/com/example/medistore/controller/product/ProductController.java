package com.example.medistore.controller.product;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import com.example.medistore.dto.product.ProductActiveRequest;
import com.example.medistore.dto.product.ProductRequest;
import com.example.medistore.dto.product.ProductResponse;
import com.example.medistore.service.product.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    // Thêm sản phẩm mới (kèm theo unit của sản phẩm đó)
    @PostMapping 
    public ProductResponse create(@RequestBody ProductRequest request) {
        return productService.createProduct(request);
    }

    // Cập nhật sản phẩm (kèm theo unit của sản phẩm đó)
    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable UUID id, @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @PatchMapping("/{id}/active")
    public ProductResponse updateActive(@PathVariable UUID id, @RequestBody ProductActiveRequest request) {
        return productService.updateProductActive(id, request.getIsActive());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        productService.deleteProduct(id);
    }

    @GetMapping
    public List<ProductResponse> getAll() {
        return productService.getAllProducts();
    }

    @GetMapping("/search")
    public List<ProductResponse> search(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }
}
