package com.anshu.userservice.listner;


import com.anshu.userservice.devicecache.model.CachedDevice;
import com.anshu.userservice.devicecache.model.DeviceStatus;
import com.anshu.userservice.devicecache.repository.CachedDeviceRepository;
import com.anshu.userservice.event.DeviceRegisteredEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceRegisteredEventListener {

    private final ObjectMapper objectMapper;
    private final CachedDeviceRepository cachedDeviceRepository;

    // Ye method ab simple call karne ke liye
    public void handleDeviceRegisteredEvent(String payload) {
        try {
            log.info("📥 Received DeviceRegisteredEvent | payload={}", payload);

            // Parse JSON to POJO
            DeviceRegisteredEvent event = objectMapper.readValue(payload, DeviceRegisteredEvent.class);

            // DB insert / update
            Optional<CachedDevice> optionalDevice = cachedDeviceRepository.findByUuid(event.deviceUuid());
            CachedDevice dbDevice;

            if (optionalDevice.isPresent()) {
                dbDevice = optionalDevice.get();
                log.info("⚠ Device already exists, updating fields | uuid={}", event.deviceUuid());
            } else {
                dbDevice = CachedDevice.builder()
                        .uuid(event.deviceUuid())
                        .createdAt(LocalDateTime.now())
                        .build();
                log.info("✅ New device, creating | uuid={}", event.deviceUuid());
            }

            // Update fields
            dbDevice.setName(event.deviceName());
            dbDevice.setDeviceType(event.deviceType());
            dbDevice.setSecret(event.deviceSecret());
            dbDevice.setStatus(DeviceStatus.valueOf(event.status())); // convert String → Enum
            dbDevice.setHouseUuid(event.houseId());
            dbDevice.setUpdatedAt(LocalDateTime.now());

            cachedDeviceRepository.save(dbDevice);

            log.info("🗄 Device saved/updated in DB | uuid={}", event.deviceUuid());

        } catch (Exception e) {
            log.error("❌ Error processing DeviceRegisteredEvent", e);
        }
    }
}