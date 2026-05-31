package com.anshu.userservice.devicecache.repository;

import com.anshu.userservice.devicecache.model.CachedDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CachedDeviceRepository extends JpaRepository<CachedDevice, Long> {
    // Optional: find by UUID
    Optional<CachedDevice> findByUuid(UUID uuid);
}