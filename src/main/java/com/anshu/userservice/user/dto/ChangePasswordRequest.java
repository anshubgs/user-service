package com.anshu.userservice.user.dto;

import lombok.Builder;

@Builder
public record ChangePasswordRequest(String oldPassword, String newPassword) {}