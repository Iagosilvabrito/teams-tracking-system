package com.tracking.backend.checkin.entity;

import com.tracking.backend.agent.entity.Agent;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "check_ins")
public class CheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "agent_id", nullable = false)
    private Agent agent;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lng;

    private String address;

    private String notes;

    @Column(name = "checked_in_at")
    private LocalDateTime checkedInAt;

    @PrePersist
    public void prePersist() {
        checkedInAt = LocalDateTime.now();
    }
}