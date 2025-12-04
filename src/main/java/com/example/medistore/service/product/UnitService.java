package com.example.medistore.service.product;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.example.medistore.entity.product.Unit;
import com.example.medistore.repository.product.UnitRepository;

@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;

    public Unit create(Unit unit) {
        return unitRepository.save(unit);
    }

    public Unit update(UUID id, Unit updated) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unit not found"));

        unit.setName(updated.getName());
        unit.setDescription(updated.getDescription());

        return unitRepository.save(unit);
    }

    public void delete(UUID id) {
        unitRepository.deleteById(id);
    }

    public List<Unit> getAll() {
        return unitRepository.findAll();
    }

    public Unit getById(UUID id) {
        return unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unit not found"));
    }
}
