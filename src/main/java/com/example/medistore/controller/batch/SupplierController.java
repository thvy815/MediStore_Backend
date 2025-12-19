package com.example.medistore.controller.batch;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.medistore.entity.batch.Supplier;
import com.example.medistore.service.batch.SupplierService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Supplier create(@RequestBody Supplier supplier) {
        return supplierService.create(supplier);
    }

    @PutMapping("/{id}")
    public Supplier update(
            @PathVariable UUID id,
            @RequestBody Supplier supplier) {
        return supplierService.update(id, supplier);
    }

    @GetMapping("/{id}")
    public Supplier getById(@PathVariable UUID id) {
        return supplierService.getById(id);
    }

    @GetMapping
    public List<Supplier> getAll() {
        return supplierService.getAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        supplierService.delete(id);
    }
}

