package com.jiwhiz.demo.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResetPasswordDTO(
    String key,

    @NotNull
    @NotBlank
    String newPassword
) {}
