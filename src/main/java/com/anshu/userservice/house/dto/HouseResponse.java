package com.anshu.userservice.house.dto;

import lombok.Builder;
import java.util.UUID;

@Builder
public record HouseResponse(
        UUID uuid,
        String name,
        UUID ownerUuid
) {}
