package com.example.medistore.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.medistore.entity.product.Category;
import com.example.medistore.repository.product.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category create(Category category) {
        return categoryRepository.save(category);
    }

    public Category update(UUID id, Category newCategory) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(newCategory.getName());
        category.setDescription(newCategory.getDescription());

        return categoryRepository.save(category);
    }

    public void delete(UUID id) {
        categoryRepository.deleteById(id);
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category getById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }
}
