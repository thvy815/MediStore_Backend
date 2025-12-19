package com.example.medistore.service.product;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.medistore.dto.product.ProductUnitResponse;
import com.example.medistore.entity.product.ProductUnit;
import com.example.medistore.repository.product.ProductUnitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductUnitService {

    private final ProductUnitRepository productUnitRepository;

    /**
     * Admin / kho: lấy TẤT CẢ unit của product
     */
    public List<ProductUnitResponse> getUnitsByProduct(UUID productId) {
        return productUnitRepository.findByProductId(productId)
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    /**
     * Customer / bán hàng: chỉ lấy unit active
     */
    public List<ProductUnitResponse> getActiveUnitsByProduct(UUID productId) {
        return productUnitRepository.findByProductIdAndIsActiveTrue(productId)
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    private ProductUnitResponse mapToResponse(ProductUnit unit) {
        ProductUnitResponse res = new ProductUnitResponse();
        res.setId(unit.getId());
        res.setUnitId(unit.getUnit().getId());
        res.setUnitName(unit.getUnit().getName());
        res.setConversionFactor(unit.getConversionFactor());
        res.setPrice(unit.getPrice());
        res.setIsDefault(unit.getIsDefault());
        res.setIsActive(unit.getIsActive());
        return res;
    }
}
