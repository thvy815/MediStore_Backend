package com.example.medistore.service.batch;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.medistore.entity.batch.Law;
import com.example.medistore.repository.batch.LawRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LawService {

    private final LawRepository lawRepository;

    // CREATE
    public void create(Law law) {
        if (lawRepository.existsById(law.getCode())) {
            throw new RuntimeException("Law code already exists");
        }
        lawRepository.save(law);
    }

    // UPDATE
    public void update(String code, Law req) {
        Law law = lawRepository.findById(code)
            .orElseThrow(() -> new RuntimeException("Law not found"));

        if (req.getTitle() != null)
            law.setTitle(req.getTitle());

        if (req.getDescription() != null)
            law.setDescription(req.getDescription());
    }

    // DELETE
    public void delete(String code) {
        Law law = lawRepository.findById(code)
            .orElseThrow(() -> new RuntimeException("Law not found"));

        lawRepository.delete(law);
    }

    // GET ONE
    @Transactional(readOnly = true)
    public Law getByCode(String code) {
        return lawRepository.findById(code)
            .orElseThrow(() -> new RuntimeException("Law not found"));
    }

    // GET ALL
    @Transactional(readOnly = true)
    public List<Law> getAll() {
        return lawRepository.findAll();
    }
}
