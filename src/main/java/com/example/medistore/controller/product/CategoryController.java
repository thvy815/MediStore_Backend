package com.example.medistore.controller.product;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import com.example.medistore.entity.product.Category;
import com.example.medistore.service.product.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customer/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<Category> getAll() {
        return categoryService.getAll();
    }

    @GetMapping("/{id}")
    public Category getById(@PathVariable UUID id) {
        return categoryService.getById(id);
    }
}
