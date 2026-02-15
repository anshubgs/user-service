package com.anshu.userservice.user.dto;

import java.util.UUID;

import lombok.Builder;

@Builder
public record UserResponse(
        UUID uuid,
        String fullName,
        String email,
        String phoneNumber,
        UUID houseUuid
) {}
