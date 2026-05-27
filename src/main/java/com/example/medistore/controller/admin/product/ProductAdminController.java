package com.example.medistore.controller.admin.product;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import com.example.medistore.dto.product.ProductActiveRequest;
import com.example.medistore.dto.product.ProductRequest;
import com.example.medistore.dto.product.ProductResponse;
import com.example.medistore.service.product.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class ProductAdminController {
    private final ProductService productService;
    
    // Lấy tất cả sản phẩm (admin)
    @GetMapping
    public List<ProductResponse> getAll() {
        return productService.getAllProducts();
    }

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

    // Update active status của sản phẩm
    @PatchMapping("/{id}/active")
    public ProductResponse updateActive(@PathVariable UUID id, @RequestBody ProductActiveRequest request) {
        return productService.updateProductActive(id, request.getIsActive());
    }

    // Xóa sản phẩm
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        productService.deleteProduct(id);
    }
}
