package com.example.medistore.entity.chat;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.medistore.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chat_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "type", length = 50)
    private String type; // ai, support, pharmacist

    @Column(name = "status", length = 50)
    private String status; // active, closed

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;
}
