package com.example.medistore.controller.prescription;

import com.example.medistore.dto.prescription.PrescriptionResponse;
import com.example.medistore.service.prescription.PrescriptionService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PrescriptionResponse upload(
            @RequestParam UUID userId,
            @RequestParam("file") MultipartFile file
    ) {

        return prescriptionService.uploadPrescription(userId, file);
    }

    @GetMapping("/user/{userId}")
    public List<PrescriptionResponse> getUserPrescriptions(
            @PathVariable UUID userId
    ) {
        return prescriptionService.getUserPrescriptions(userId);
    }

    @PutMapping("/{id}/approve")
    public PrescriptionResponse approve(@PathVariable UUID id) {
        return prescriptionService.approvePrescription(id);
    }

    @PutMapping("/{id}/reject")
    public PrescriptionResponse reject(
            @PathVariable UUID id,
            @RequestParam String reason
    ) {
        return prescriptionService.rejectPrescription(id, reason);
    }
}