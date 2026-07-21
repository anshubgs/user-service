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

    public void handleDeviceRegisteredEvent(String payload) {
        try {

            log.info("======================================================");
            log.info("📥 Incoming Payload : {}", payload);
            log.info("======================================================");

            // JSON -> POJO
            DeviceRegisteredEvent event =
                    objectMapper.readValue(payload, DeviceRegisteredEvent.class);

            log.info("========== DESERIALIZED EVENT ==========");
            log.info("deviceUuid   : {}", event.deviceUuid());
            log.info("deviceName   : {}", event.deviceName());
            log.info("deviceType   : {}", event.deviceType());
            log.info("deviceSecret : {}", event.deviceSecret());
            log.info("status       : {}", event.status());
            log.info("registeredAt : {}", event.registeredAt());
            log.info("houseUuid    : {}", event.houseUuid());
            log.info("userUuid     : {}", event.userUuid());
            log.info("========================================");

            Optional<CachedDevice> optionalDevice =
                    cachedDeviceRepository.findByUuid(event.deviceUuid());

            CachedDevice dbDevice;

            if (optionalDevice.isPresent()) {

                dbDevice = optionalDevice.get();

                log.info("⚠ Existing Device Found");
                log.info("DB houseUuid BEFORE update = {}", dbDevice.getHouseUuid());

            } else {

                dbDevice = CachedDevice.builder()
                        .uuid(event.deviceUuid())
                        .createdAt(LocalDateTime.now())
                        .build();

                log.info("✅ Creating New Device");
            }

            dbDevice.setName(event.deviceName());
            dbDevice.setDeviceType(event.deviceType());
            dbDevice.setSecret(event.deviceSecret());
            dbDevice.setStatus(DeviceStatus.valueOf(event.status()));
            dbDevice.setHouseUuid(event.houseUuid());
            dbDevice.setUpdatedAt(LocalDateTime.now());

            log.info("========== ENTITY BEFORE SAVE ==========");
            log.info("uuid         : {}", dbDevice.getUuid());
            log.info("houseUuid    : {}", dbDevice.getHouseUuid());
            log.info("name         : {}", dbDevice.getName());
            log.info("deviceType   : {}", dbDevice.getDeviceType());
            log.info("secret       : {}", dbDevice.getSecret());
            log.info("status       : {}", dbDevice.getStatus());
            log.info("createdAt    : {}", dbDevice.getCreatedAt());
            log.info("updatedAt    : {}", dbDevice.getUpdatedAt());
            log.info("========================================");

            CachedDevice saved = cachedDeviceRepository.save(dbDevice);

            log.info("========== SAVED SUCCESSFULLY ==========");
            log.info("DB ID        : {}", saved.getId());
            log.info("UUID         : {}", saved.getUuid());
            log.info("House UUID   : {}", saved.getHouseUuid());
            log.info("========================================");

        } catch (Exception e) {
            log.error("❌ Error processing DeviceRegisteredEvent", e);
            throw new RuntimeException(e);
        }
    }
}