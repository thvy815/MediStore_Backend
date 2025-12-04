package com.example.medistore.entity.product;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "brands")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Brand {
    @Id @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 255, unique = true)
    private String name;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
