package com.anshu.userservice.user.dto;

import lombok.Builder;

@Builder
public record AuthResponse(
        String accessToken,
        boolean requiresHouseSetup
) {}
