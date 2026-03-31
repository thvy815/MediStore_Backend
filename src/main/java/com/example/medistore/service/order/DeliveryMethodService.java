package com.example.medistore.service.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.medistore.dto.order.CreateDeliveryMethodRequest;
import com.example.medistore.dto.order.DeliveryMethodResponse;
import com.example.medistore.dto.order.UpdateDeliveryMethodRequest;
import com.example.medistore.entity.order.DeliveryMethod;
import com.example.medistore.repository.order.DeliveryMethodRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryMethodService {

    private final DeliveryMethodRepository deliveryMethodRepository;

    // GET ALL
    public List<DeliveryMethodResponse> getAllDeliveryMethods() {
        return deliveryMethodRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    // GET BY ID
    public DeliveryMethodResponse getDeliveryMethodById(UUID id) {
        DeliveryMethod method = deliveryMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery method not found"));
        return mapToResponse(method);
    }

    // CREATE
    public DeliveryMethodResponse createDeliveryMethod(CreateDeliveryMethodRequest request) {
        DeliveryMethod method = DeliveryMethod.builder()
                .name(request.getName())
                .description(request.getDescription())
                .baseFee(request.getBaseFee())
                .estimatedDays(request.getEstimatedDays())
                .isActive(request.getIsActive())
                .build();
        method = deliveryMethodRepository.save(method);
        return mapToResponse(method);
    }

    // UPDATE
    public DeliveryMethodResponse updateDeliveryMethod(UUID id, UpdateDeliveryMethodRequest request) {
        DeliveryMethod method = deliveryMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery method not found"));
        method.setName(request.getName());
        method.setDescription(request.getDescription());
        method.setBaseFee(request.getBaseFee());
        method.setEstimatedDays(request.getEstimatedDays());
        method.setIsActive(request.getIsActive());
        method = deliveryMethodRepository.save(method);
        return mapToResponse(method);
    }

    // DELETE
    public void deleteDeliveryMethod(UUID id) {
        if (!deliveryMethodRepository.existsById(id)) {
            throw new RuntimeException("Delivery method not found");
        }
        deliveryMethodRepository.deleteById(id);
    }

    // MAPPING
    private DeliveryMethodResponse mapToResponse(DeliveryMethod method) {
        return DeliveryMethodResponse.builder()
                .id(method.getId())
                .name(method.getName())
                .description(method.getDescription())
                .baseFee(method.getBaseFee())
                .estimatedDays(method.getEstimatedDays())
                .isActive(method.getIsActive())
                .build();
    }
}