package com.example.medistore.controller.admin.product;

import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import com.example.medistore.entity.product.Brand;
import com.example.medistore.service.product.BrandService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/brands")
@RequiredArgsConstructor
public class BrandAdminController {
    private final BrandService brandService;

    @PostMapping
    public Brand create(@RequestBody Brand brand) {
        return brandService.create(brand);
    }

    @PutMapping("/{id}")
    public Brand update(@PathVariable UUID id, @RequestBody Brand brand) {
        return brandService.update(id, brand);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        brandService.delete(id);
    }
}
