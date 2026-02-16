package com.anshu.userservice.event;

import java.util.UUID;

import lombok.Builder;

@Builder
public record UserSyncedEvent(
		String eventType, 
        UUID userUuid,
        UUID houseUuid,
        String role,
        String status
) {}
