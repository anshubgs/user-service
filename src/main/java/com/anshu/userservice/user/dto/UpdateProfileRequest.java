package com.anshu.userservice.user.dto;

import lombok.Builder;

@Builder
public record UpdateProfileRequest(String fullName, String phoneNumber) {}