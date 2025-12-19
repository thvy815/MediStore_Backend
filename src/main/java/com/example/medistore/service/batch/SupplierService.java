package com.example.medistore.service.batch;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.medistore.entity.batch.Supplier;
import com.example.medistore.repository.batch.SupplierRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    // CREATE
    public Supplier create(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    // UPDATE
    public Supplier update(UUID id, Supplier supplier) {
        Supplier existing = supplierRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Supplier not found"));

        existing.setName(supplier.getName());
        existing.setPhone(supplier.getPhone());
        existing.setAddress(supplier.getAddress());

        return supplierRepository.save(existing);
    }

    // GET BY ID
    public Supplier getById(UUID id) {
        return supplierRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Supplier not found"));
    }

    // GET ALL
    public List<Supplier> getAll() {
        return supplierRepository.findAll();
    }

    // DELETE
    public void delete(UUID id) {
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Supplier not found");
        }
        supplierRepository.deleteById(id);
    }
}
