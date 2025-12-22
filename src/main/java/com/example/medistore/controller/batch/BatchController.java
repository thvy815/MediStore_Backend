package com.example.medistore.controller.batch;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.medistore.dto.batch.BatchResponse;
import com.example.medistore.dto.batch.CreateBatchRequest;
import com.example.medistore.dto.batch.UpdateBatchRequest;
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

    // Lấy thông tin lô hàng theo ID
    @GetMapping("/{id}")
    public ResponseEntity<BatchResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(batchService.getBatchById(id));
    }

    // Lấy danh sách lô hàng với tùy chọn lọc
    @GetMapping
    public ResponseEntity<List<BatchResponse>> getAll(
        @RequestParam(required = false) UUID productId,
        @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(
            batchService.getAllBatches(productId, status)
        );
    }

    // Cập nhật thông tin lô hàng
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
        @PathVariable UUID id,
        @RequestBody UpdateBatchRequest request
    ) {
        batchService.updateBatch(id, request);
        return ResponseEntity.ok().build();
    }

    // Thu hồi lô hàng (xóa mềm --> status = "recalled")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        batchService.recallBatch(id);
        return ResponseEntity.noContent().build();
    }

    // Danh sách product còn hàng trong kho
    @GetMapping("/products/in-stock")
    public ResponseEntity<List<UUID>> getProductsInStock() {
        return ResponseEntity.ok(batchService.getProductIdsInStock());
    }
}
