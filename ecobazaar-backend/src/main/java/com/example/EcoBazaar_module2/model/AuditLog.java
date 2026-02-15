package com.example.EcoBazaar_module2.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String entityType;

    @Column(nullable = false)
    private Long entityId;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;
}