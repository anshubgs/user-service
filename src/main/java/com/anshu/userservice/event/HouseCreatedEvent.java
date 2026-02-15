package com.anshu.userservice.event;

import java.util.UUID;

import lombok.Builder;

@Builder
public record HouseCreatedEvent(
        UUID houseUuid,
        UUID ownerUuid,
        String name
) {}

