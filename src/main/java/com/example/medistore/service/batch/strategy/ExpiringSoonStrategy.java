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
public class ExpiringSoonStrategy
        implements BatchInventoryStrategy {

    private final BatchRepository batchRepository;
    private final BatchMapper batchMapper;

    private static final int EXPIRY_WARNING_DAYS = 30;

    @Override
    public List<BatchResponse> execute() {

        LocalDate now = LocalDate.now();

        LocalDate warningDate = now.plusDays(
                EXPIRY_WARNING_DAYS);

        return batchRepository
                .findExpiringSoon(
                        now,
                        warningDate)
                .stream()
                .map(batchMapper::toResponse)
                .toList();
    }
}