package com.tracking.backend.sync.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "sync_tokens")
public class SyncToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sync_type", unique = true, nullable = false)
    private String syncType;

    @Column(nullable = false)
    private String token;
}