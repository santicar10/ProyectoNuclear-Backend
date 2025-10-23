package com.huahuacuna.app.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para el registro de usuarios: solo correo, contraseña y nombre.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroDTO {

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener formato válido")
    private String correo;

    /**
     * Marcar como write-only evita que Jackson serialice la contraseña en respuestas JSON.
     * Si no usas Jackson, puedes usar @JsonIgnore en su lugar.
     */
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String contrasena;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
}