package com.anshu.userservice.user.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record LoginRequest(
        @Email @NotBlank String email,
        @NotBlank String password
) {}
