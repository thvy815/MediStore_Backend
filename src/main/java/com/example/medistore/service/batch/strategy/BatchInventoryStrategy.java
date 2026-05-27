package com.example.medistore.service.batch.strategy;

import java.util.List;
import com.example.medistore.dto.batch.BatchResponse;

public interface BatchInventoryStrategy {

    List<BatchResponse> execute();
}