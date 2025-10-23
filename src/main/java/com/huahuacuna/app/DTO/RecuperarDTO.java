package com.huahuacuna.app.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para la solicitud de recuperación de contraseña.
 *
 * <p>Contiene únicamente el correo del usuario que solicita la recuperación.
 * Se usa en endpoints como `/api/usuarios/recuperar` para iniciar el flujo de
 * restablecimiento.</p>
 *
 * Validaciones:
 * - correo: no puede estar en blanco. (Se recomienda adicionalmente validar formato con @Email
 *   si se desea.)
 *
 * Seguridad:
 * - No enviar contraseñas por correo en texto plano en producción; preferir token
 *   de recuperación con expiración y enlace para restablecer la contraseña.
 */
@Getter
@Setter
public class RecuperarDTO {

    @NotBlank(message = "El correo no puede estar vacío")
    private String correo;
}