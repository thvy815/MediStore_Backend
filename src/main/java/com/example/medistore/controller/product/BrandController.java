package com.example.medistore.controller.product;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import com.example.medistore.entity.product.Brand;
import com.example.medistore.service.product.BrandService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customer/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public List<Brand> getAll() {
        return brandService.getAll();
    }

    @GetMapping("/{id}")
    public Brand getById(@PathVariable UUID id) {
        return brandService.getById(id);
    }
}
