package com.example.medistore.controller.batch;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.medistore.entity.batch.Law;
import com.example.medistore.service.batch.LawService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/laws")
@RequiredArgsConstructor
public class LawController {

    private final LawService lawService;

    // CREATE
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody Law law) {
        lawService.create(law);
        return ResponseEntity.ok().build();
    }

    // UPDATE
    @PutMapping("/{code}")
    public ResponseEntity<Void> update(
        @PathVariable String code,
        @RequestBody Law law
    ) {
        lawService.update(code, law);
        return ResponseEntity.ok().build();
    }

    // DELETE
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        lawService.delete(code);
        return ResponseEntity.noContent().build();
    }

    // GET ONE
    @GetMapping("/{code}")
    public ResponseEntity<Law> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(lawService.getByCode(code));
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<Law>> getAll() {
        return ResponseEntity.ok(lawService.getAll());
    }
}
