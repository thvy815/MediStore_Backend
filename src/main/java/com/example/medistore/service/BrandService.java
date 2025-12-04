package com.example.medistore.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.medistore.entity.product.Brand;
import com.example.medistore.repository.product.BrandRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    public Brand create(Brand brand) {
        return brandRepository.save(brand);
    }

    public Brand update(UUID id, Brand newBrand) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        brand.setName(newBrand.getName());
        brand.setCountry(newBrand.getCountry());
        brand.setDescription(newBrand.getDescription());

        return brandRepository.save(brand);
    }

    public void delete(UUID id) {
        brandRepository.deleteById(id);
    }

    public List<Brand> getAll() {
        return brandRepository.findAll();
    }

    public Brand getById(UUID id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));
    }
}
