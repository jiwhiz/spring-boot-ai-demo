package com.jiwhiz.demo.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * A DTO representing a new user registration form.
 */
@Builder
public record RegistrationDTO (
        @NotBlank
        @Email
        @Size(min = 5, max = 255)
        String email,

        @NotNull
        @NotBlank
        @Size(min = 4, max = 20)
        String password,

        @Size(max = 100)
        String firstName,

        @Size(max = 100)
        String lastName
) {}

