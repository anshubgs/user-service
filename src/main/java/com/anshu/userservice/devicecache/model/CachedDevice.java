package com.anshu.userservice.devicecache.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cached_device", schema = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CachedDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Column(name = "house_uuid", nullable = false)
    private UUID houseUuid;

    @Column(nullable = false, length = 100)
    private String secret;

    @Column(length = 100)
    private String name;

    @Column(name = "device_type", length = 50)
    private String deviceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeviceStatus status;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (uuid == null) uuid = UUID.randomUUID();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = DeviceStatus.INACTIVE;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
