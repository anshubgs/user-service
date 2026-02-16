package com.anshu.userservice.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateMemberRequest(

        @NotBlank
        String fullName,

        @Email
        @NotBlank
        String email,

        @NotBlank
        String password,

        String phoneNumber
) {}
