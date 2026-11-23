package com.machforms.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "machforms.security.jwt")
public record JwtProperties(
        @NotBlank
        @Size(min = 32, message = "o segredo JWT precisa ter ao menos 32 caracteres")
        String secret,

        @NotNull
        Duration expiration
) {
}
