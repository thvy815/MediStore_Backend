package com.example.medistore.service.batch.strategy;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.medistore.dto.batch.BatchResponse;
import com.example.medistore.service.batch.BatchMapper;
import com.example.medistore.repository.batch.BatchRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LowStockStrategy
        implements BatchInventoryStrategy {

    private final BatchRepository batchRepository;
    private final BatchMapper batchMapper;

    private static final int LOW_STOCK_THRESHOLD = 100;

    @Override
    public List<BatchResponse> execute() {

        return batchRepository
                .findByStatusAndQuantityRemainingLessThan(
                        "valid",
                        LOW_STOCK_THRESHOLD)
                .stream()
                .map(batchMapper::toResponse)
                .toList();
    }
}