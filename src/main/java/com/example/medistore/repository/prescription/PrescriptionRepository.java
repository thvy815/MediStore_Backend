package com.example.medistore.repository.prescription;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.medistore.entity.prescription.Prescription;

import java.util.List;
import java.util.UUID;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

    List<Prescription> findByUserId(UUID userId);
}