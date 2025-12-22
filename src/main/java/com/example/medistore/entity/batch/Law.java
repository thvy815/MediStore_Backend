package com.example.medistore.entity.batch;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "laws")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Law {
    @Id 
    @Column(name = "code", nullable = false, length = 50)
    private String code; // VD: TT_02_2023_BYT

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
