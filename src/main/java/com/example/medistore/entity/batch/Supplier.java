package com.example.medistore.entity.batch;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "suppliers")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Supplier {
    @Id @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "phone", length = 50)
    private String phone;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
}
