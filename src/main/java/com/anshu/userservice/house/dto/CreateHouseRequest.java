package com.anshu.userservice.house.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateHouseRequest(
        @NotBlank String name
) {}
