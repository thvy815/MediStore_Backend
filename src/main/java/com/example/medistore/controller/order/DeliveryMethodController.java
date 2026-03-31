package com.example.medistore.controller.order;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.example.medistore.dto.order.CreateDeliveryMethodRequest;
import com.example.medistore.dto.order.DeliveryMethodResponse;
import com.example.medistore.dto.order.UpdateDeliveryMethodRequest;
import com.example.medistore.service.order.DeliveryMethodService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/delivery-methods")
@RequiredArgsConstructor
public class DeliveryMethodController {

    private final DeliveryMethodService deliveryMethodService;

    // GET ALL
    @GetMapping
    public List<DeliveryMethodResponse> getAllDeliveryMethods() {
        return deliveryMethodService.getAllDeliveryMethods();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public DeliveryMethodResponse getDeliveryMethodById(@PathVariable UUID id) {
        return deliveryMethodService.getDeliveryMethodById(id);
    }

    // CREATE
    @PostMapping
    public DeliveryMethodResponse createDeliveryMethod(@RequestBody CreateDeliveryMethodRequest request) {
        return deliveryMethodService.createDeliveryMethod(request);
    }

    // UPDATE
    @PutMapping("/{id}")
    public DeliveryMethodResponse updateDeliveryMethod(@PathVariable UUID id, @RequestBody UpdateDeliveryMethodRequest request) {
        return deliveryMethodService.updateDeliveryMethod(id, request);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deleteDeliveryMethod(@PathVariable UUID id) {
        deliveryMethodService.deleteDeliveryMethod(id);
    }
}