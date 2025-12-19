package com.example.medistore.controller.batch;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.medistore.dto.batch.BatchResponse;
import com.example.medistore.dto.batch.CreateBatchRequest;
import com.example.medistore.service.batch.BatchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/batches")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    // Nhập kho (tạo lô mới)
    @PostMapping
    public ResponseEntity<Void> createBatch(@RequestBody CreateBatchRequest request) {
        batchService.createBatch(request);
        return ResponseEntity.ok().build();
    }

    // Lấy danh sách lô hàng của 1 sản phẩm
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<BatchResponse>> getBatches(@PathVariable UUID productId) {
        return ResponseEntity.ok(
            batchService.getBatchesByProduct(productId)
        );
    }

    // Danh sách product còn hàng trong kho
    @GetMapping("/products/in-stock")
    public ResponseEntity<List<UUID>> getProductsInStock() {
        return ResponseEntity.ok(batchService.getProductIdsInStock());
    }
}
