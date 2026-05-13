package com.example.medistore.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BestSellingProductDTO {

    private String productName;
    private Long quantitySold;
}