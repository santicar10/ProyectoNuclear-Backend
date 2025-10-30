package com.huahuacuna.app.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetContrasenaDTO {
    @NotBlank
    @Email
    private String correo;

    @NotBlank
    private String codigo;

    @NotBlank
    @Size(min = 6)
    private String nuevaContrasena;
}