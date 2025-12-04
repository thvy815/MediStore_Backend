package com.example.medistore.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import com.example.medistore.entity.product.Brand;
import com.example.medistore.service.BrandService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

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

    @GetMapping
    public List<Brand> getAll() {
        return brandService.getAll();
    }

    @GetMapping("/{id}")
    public Brand getById(@PathVariable UUID id) {
        return brandService.getById(id);
    }
}
