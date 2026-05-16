package com.example.medistore.dto.prescription;

import com.example.medistore.enums.PrescriptionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PrescriptionResponse {

    private UUID id;

    private UUID userId;

    private String imageUrl;

    private PrescriptionStatus status;

    private String rejectionReason;

    private LocalDateTime createdAt;
}