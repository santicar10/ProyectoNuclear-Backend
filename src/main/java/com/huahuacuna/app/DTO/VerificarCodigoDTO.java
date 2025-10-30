package com.huahuacuna.app.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificarCodigoDTO {
    @NotBlank
    @Email
    private String correo;

    @NotBlank
    private String codigo; // 6 dígitos
}