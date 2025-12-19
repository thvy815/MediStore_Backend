package com.example.medistore.service.product;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.medistore.entity.product.Product;
import com.example.medistore.entity.product.ProductUnit;
import com.example.medistore.repository.product.*;
import com.example.medistore.dto.product.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductUnitRepository productUnitRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final UnitRepository unitRepository;

    private String normalizeCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        return code.trim();
    }

    // Create product
    public ProductResponse createProduct(ProductRequest request) {
        String code = normalizeCode(request.getCode());

        if (code != null && productRepository.existsByCode(code)) {
            throw new RuntimeException("Product code already exists");
        }

        // --- CREATE PRODUCT ---
        Product product = Product.builder()
                .code(code)
                .name(request.getName())
                .brand(brandRepository.findById(request.getBrandId()).orElse(null))
                .category(categoryRepository.findById(request.getCategoryId()).orElse(null))
                .imageUrl(request.getImageUrl())
                .description(request.getDescription())
                .ingredients(request.getIngredients())
                .prescriptionRequired(request.getPrescriptionRequired())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .createdAt(LocalDateTime.now())
                .build();

        Product savedProduct = productRepository.save(product);

        // --- ADD UNITS ---
        if (request.getUnits() != null) {
            for (ProductUnitRequest u : request.getUnits()) {

                ProductUnit unit = ProductUnit.builder()
                        .product(savedProduct)
                        .unit(unitRepository.findById(u.getUnitId())
                                .orElseThrow(() -> new RuntimeException("Unit not found")))
                        .conversionFactor(u.getConversionFactor())
                        .price(u.getPrice())
                        .isDefault(u.getIsDefault() != null ? u.getIsDefault() : false)
                        .build();

                productUnitRepository.save(unit);
            }
        }
        return mapToResponse(savedProduct);
    }

    // Update
    @Transactional
    public ProductResponse updateProduct(UUID productId, ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        String code = normalizeCode(request.getCode());

        if (code != null && productRepository.existsByCode(code) && !code.equals(product.getCode())) {
            throw new RuntimeException("Product code already exists");
        }

        // UPDATE PRODUCT FIELDS
        product.setCode(code); 
        product.setName(request.getName());
        product.setBrand(brandRepository.findById(request.getBrandId()).orElse(null));
        product.setCategory(categoryRepository.findById(request.getCategoryId()).orElse(null));
        product.setImageUrl(request.getImageUrl());
        product.setDescription(request.getDescription());
        product.setIngredients(request.getIngredients());
        product.setPrescriptionRequired(request.getPrescriptionRequired());
        product.setIsActive(request.getIsActive());
        product.setUpdatedAt(LocalDateTime.now());

        Product saved = productRepository.save(product);

        // --- UPDATE UNITS ---
        if (request.getUnits() != null) {
            // 1) Xoá units cũ
            productUnitRepository.deleteByProductId(productId);

            // 2) Tạo units mới
            for (ProductUnitRequest u : request.getUnits()) {
                ProductUnit unit = ProductUnit.builder()
                        .product(saved)
                        .unit(unitRepository.findById(u.getUnitId())
                                .orElseThrow(() -> new RuntimeException("Unit not found")))
                        .conversionFactor(u.getConversionFactor())
                        .price(u.getPrice())
                        .isDefault(u.getIsDefault() != null ? u.getIsDefault() : false)
                        .build();

                productUnitRepository.save(unit);
            }
        }
        
        return mapToResponse(saved);
    }

    // Delete
    public void deleteProduct(UUID productId) {
        productRepository.deleteById(productId);
    }

    // Search
    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword)
                .stream().map(this::mapToResponse).toList();
    }

    // Get all products (admin)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    // Get active products (customer)
    public List<ProductResponse> getActiveProducts() {
        return productRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Update product active
    public ProductResponse updateProductActive(UUID productId, Boolean isActive) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setIsActive(isActive);
        product.setUpdatedAt(LocalDateTime.now());

        Product saved = productRepository.save(product);

        return mapToResponse(saved);
    }

    // Mapping
    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setCode(product.getCode());
        response.setName(product.getName());
        response.setBrandName(product.getBrand() != null ? product.getBrand().getName() : null);
        response.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);
        response.setImageUrl(product.getImageUrl());
        response.setDescription(product.getDescription());
        response.setIngredients(product.getIngredients());
        response.setPrescriptionRequired(product.getPrescriptionRequired());
        response.setIsActive(product.getIsActive());

        response.setUnits(productUnitRepository.findByProductId(product.getId())
                .stream()
                .map(u -> {
                    ProductUnitResponse ur = new ProductUnitResponse();
                    ur.setId(u.getId());
                    ur.setUnitName(u.getUnit().getName());
                    ur.setConversionFactor(u.getConversionFactor());
                    ur.setPrice(u.getPrice());
                    ur.setIsDefault(u.getIsDefault());
                    return ur;
                }).toList());

        return response;
    }
}
