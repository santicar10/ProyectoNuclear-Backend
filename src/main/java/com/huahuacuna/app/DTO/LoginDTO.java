package com.huahuacuna.app.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO usado para la operación de autenticación (login).
 *
 * <p>Contiene los datos mínimos necesarios que el cliente debe enviar
 * para intentar iniciar sesión: correo y contraseña.</p>
 *
 * Validaciones:
 * - correo: debe ser un email válido y no estar vacío.
 * - contrasena: no puede estar en blanco.
 *
 * Uso típico:
 * - Se utiliza como @RequestBody en el endpoint de login:
 *   public ResponseEntity<?> login(@RequestBody @Valid LoginDTO loginRequest, HttpSession session)
 *
 * Seguridad:
 * - La contraseña llega en texto plano desde el cliente; debe compararse contra
 *   un hash almacenado en la base de datos (usar BCrypt). No almacenar ni loggear
 *   la contraseña en texto plano.
 */
@Getter
@Setter
public class LoginDTO {

    @Email(message = "El correo debe tener un formato válido")
    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;

}