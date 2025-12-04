package com.example.medistore.controller.product;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import com.example.medistore.entity.product.Unit;
import com.example.medistore.service.product.UnitService;

@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    @PostMapping
    public Unit create(@RequestBody Unit req) {
        return unitService.create(req);
    }

    @PutMapping("/{id}")
    public Unit update(@PathVariable UUID id, @RequestBody Unit req) {
        return unitService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        unitService.delete(id);
    }

    @GetMapping
    public List<Unit> getAll() {
        return unitService.getAll();
    }

    @GetMapping("/{id}")
    public Unit getById(@PathVariable UUID id) {
        return unitService.getById(id);
    }
}
