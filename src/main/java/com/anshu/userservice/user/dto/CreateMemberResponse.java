package com.anshu.userservice.user.dto;

import java.util.UUID;

import lombok.Builder;

@Builder
public record CreateMemberResponse(
        UUID userUuid,
        String fullName,
        String email,
        String role,
        UUID houseUuid
) {}
