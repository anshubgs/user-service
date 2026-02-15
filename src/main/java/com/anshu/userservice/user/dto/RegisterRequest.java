package com.anshu.userservice.user.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record RegisterRequest(
        @NotBlank String fullName,
        @Email @NotBlank String email,
        @NotBlank @Size(min = 8) String password,
        @NotBlank String phoneNumber
) {}
