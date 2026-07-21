package com.anshu.userservice.event;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;

@Builder
public record DeviceRegisteredEvent( UUID deviceUuid,
 String deviceName,
 String deviceType,
 String deviceSecret,
 String status,
 Instant registeredAt,
 UUID houseUuid,
 UUID userUuid) {

}
