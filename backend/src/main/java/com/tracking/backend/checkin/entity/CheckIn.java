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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckInType type = CheckInType.CHECKIN;

    @Column(nullable = false)
    private String source = "MANUAL";

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lng;

    private String address;

    private Double accuracy;

    private Double speed;

    private String notes;

    @Column(name = "distance_from_previous")
    private Double distanceFromPrevious;

    @Column(name = "external_event_id", unique = true)
    private String externalEventId;

    @Column(name = "occurred_at")
    private LocalDateTime occurredAt;

    @Column(name = "synced_at")
    private LocalDateTime syncedAt;

    @Column(name = "checked_in_at")
    private LocalDateTime checkedInAt;

    @PrePersist
    public void prePersist() {
        if (checkedInAt == null) checkedInAt = LocalDateTime.now();
        if (occurredAt == null) occurredAt = checkedInAt;
    }
}