package com.example.medistore.service.batch.strategy;

import java.time.LocalDate;
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
public class InStockStrategy
        implements BatchInventoryStrategy {

    private final BatchRepository batchRepository;
    private final BatchMapper batchMapper;

    @Override
    public List<BatchResponse> execute() {

        return batchRepository
                .findBatchesInStock(
                        LocalDate.now())
                .stream()
                .map(batchMapper::toResponse)
                .toList();
    }
}