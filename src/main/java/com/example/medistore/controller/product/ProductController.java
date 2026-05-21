package com.example.medistore.controller.product;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import com.example.medistore.dto.product.ProductResponse;
import com.example.medistore.service.product.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customer/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    // Lấy chi tiết sản phẩm theo ID 
    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable UUID id) {
        return productService.getProductById(id);
    }  

    //Lấy danh sách sản phẩm đang active
    @GetMapping("/active")
    public List<ProductResponse> getActiveProducts() {
        return productService.getActiveProducts();
    }

    // Tìm kiếm sản phẩm theo keyword 
    @GetMapping("/search")
    public List<ProductResponse> search(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }
}
