package com.example.medistore.service.prescription;

import com.example.medistore.dto.prescription.PrescriptionResponse;
import com.example.medistore.entity.prescription.Prescription;
import com.example.medistore.entity.user.User;
import com.example.medistore.enums.PrescriptionStatus;
import com.example.medistore.repository.prescription.PrescriptionRepository;
import com.example.medistore.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;

    // mapper
    private PrescriptionResponse toResponse(Prescription p) {
        return PrescriptionResponse.builder()
                .id(p.getId())
                .userId(p.getUser().getId())
                .imageUrl(p.getImageUrl())
                .status(p.getStatus())
                .rejectionReason(p.getRejectionReason())
                .createdAt(p.getCreatedAt())
                .build();
    }

    public PrescriptionResponse uploadPrescription(UUID userId, MultipartFile file) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String imageUrl = fileUploadService.uploadFile(file);

        Prescription prescription = Prescription.builder()
                .user(user)
                .imageUrl(imageUrl)
                .status(PrescriptionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        prescriptionRepository.save(prescription);

        return toResponse(prescriptionRepository.save(prescription));
    }

    public List<PrescriptionResponse> getUserPrescriptions(UUID userId) {
        return prescriptionRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    public PrescriptionResponse approvePrescription(UUID id) {

        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        prescription.setStatus(PrescriptionStatus.APPROVED);

        return toResponse(prescriptionRepository.save(prescription));
    }

    public PrescriptionResponse rejectPrescription(UUID id, String reason) {

        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        prescription.setStatus(PrescriptionStatus.REJECTED);
        prescription.setRejectionReason(reason);

        return toResponse(prescriptionRepository.save(prescription));
    }
}