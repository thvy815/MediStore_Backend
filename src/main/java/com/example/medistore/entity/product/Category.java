package com.example.medistore.entity.product;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Category {
    @Id @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 255, unique = true)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}

