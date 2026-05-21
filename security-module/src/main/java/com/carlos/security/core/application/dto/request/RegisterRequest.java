package com.carlos.security.core.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank
    String username;

    @NotBlank
    @Email
    String email;

    @NotBlank
    @Size(min = 8)
    String password;

    @NotBlank
    String firstName;

    String lastName;

    String phoneNumber;
}
