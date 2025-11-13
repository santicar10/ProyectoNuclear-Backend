package com.huahuacuna.app.controller;

import com.huahuacuna.app.DTO.*;
import com.huahuacuna.app.model.Usuario;
import com.huahuacuna.app.service.UsuarioService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para la gestión de usuarios.
 *
 * <p>Provee endpoints para:
 * - registro de usuarios
 * - inicio de flujo de recuperación de contraseña (envío de código)
 * - verificación de código de recuperación
 * - restablecimiento de contraseña mediante código
 * - cambio de contraseña para usuarios autenticados
 * - ver perfil (basado en sesión HTTP)
 * - cierre de sesión</p>
 *
 * <p>Notas de seguridad/importantes:
 * - El endpoint de registro normaliza el correo (trim + lowercase) antes de comprobar existencia/crear.
 * - El flujo de recuperación no revela si un correo existe (respuesta intencionalmente ambigua) para evitar enumeración de usuarios.
 * - Este controlador usa HttpSession para almacenar/obtener el usuario logueado bajo el atributo "usuarioLogueado". Asegúrate de que el filtro/autenticación de la app establezca ese atributo al iniciar sesión.</p>
 *
 * @see UsuarioService
 * @see RegistroDTO
 * @see RecuperarRequestDTO
 * @see VerificarCodigoDTO
 * @see ResetContrasenaDTO
 * @see CambiarContrasenaDTO
 * @author Janka033
 * @since 1.0
 */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:3000")
public class UsuarioController {

    /**
     * Servicio que contiene la lógica de negocio para usuarios (registro, recuperación, cambios de contraseña, etc.).
     * Inyectado por Spring.
     */
    @Autowired
    private UsuarioService usuarioService;

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * <p>El correo se normaliza (trim + lowercase) antes de validar existencia. Si el correo ya está en uso
     * devuelve HTTP 409 CONFLICT con un mensaje sencillo. Si la creación es exitosa, devuelve HTTP 201 CREATED
     * con un objeto que contiene id, correo y nombre del usuario creado.</p>
     *
     * @param dto DTO con los datos de registro (correo, nombre, contraseña). Validado por {@code @Valid}.
     * @return ResponseEntity con un mapa que incluye usuarioId, correo y nombre en caso de éxito (201),
     *         o con un mensaje de error en caso de conflicto (409).
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroDTO dto) {
        String correo = dto.getCorreo().trim().toLowerCase();

        if (usuarioService.existsByCorreo(correo)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("mensaje", "El correo ya está registrado."));
        }

        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setNombre(dto.getNombre().trim());
        usuario.setContrasena(dto.getContrasena());

        Usuario saved = usuarioService.registrar(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "usuarioId", saved.getId_usuario(),
                "correo", saved.getCorreo(),
                "nombre", saved.getNombre()
        ));
    }

    /**
     * Inicia el proceso de recuperación de contraseña para un correo dado.
     *
     * <p>El servicio intentará enviar un correo con un código de recuperación. Por motivos de seguridad
     * la respuesta pública no indica si el correo existe o no; en ambos casos se devuelve 200 OK con
     * un mensaje genérico. Si ocurre un error enviando el correo se devuelve 500 INTERNAL SERVER ERROR.</p>
     *
     * @param dto DTO con el correo para iniciar la recuperación. Validado por {@code @Valid}.
     * @return ResponseEntity con mensaje genérico (200) o con mensaje de error (500) si falla el envío.
     */
    @PostMapping("/recuperar")
    public ResponseEntity<?> iniciarRecuperacion(@Valid @RequestBody RecuperarRequestDTO dto) {
        try {
            usuarioService.iniciarRecuperacion(dto.getCorreo());
            // Por seguridad no confirmamos si el correo existe o no en la respuesta pública.
            return ResponseEntity.ok(Map.of("mensaje", "Si el correo existe, se ha enviado un código de recuperación."));
        } catch (IllegalArgumentException e) {
            // Se devuelve el mismo 200 para evitar enumeración de usuarios.
            return ResponseEntity.ok(Map.of("mensaje", "Si el correo existe, se ha enviado un código de recuperación."));
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("mensaje", "Error enviando el correo."));
        }
    }

    /**
     * Verifica si un código de recuperación es válido para el correo indicado.
     *
     * @param dto DTO con correo y código a verificar. Validado por {@code @Valid}.
     * @return 200 OK con mensaje si el código es válido; 400 BAD REQUEST con mensaje si es inválido o expirado.
     */
    @PostMapping("/recuperar/verificar")
    public ResponseEntity<?> verificarCodigo(@Valid @RequestBody VerificarCodigoDTO dto) {
        boolean ok = usuarioService.verificarCodigo(dto.getCorreo(), dto.getCodigo());
        if (ok) {
            return ResponseEntity.ok(Map.of("mensaje", "Código válido."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", "Código inválido o expirado."));
        }
    }

    /**
     * Restablece la contraseña utilizando el correo, el código de recuperación y la nueva contraseña.
     *
     * @param dto DTO con correo, código y nueva contraseña. Validado por {@code @Valid}.
     * @return 200 OK con mensaje si la contraseña fue restablecida; 400 BAD REQUEST si el código es inválido/expirado.
     */
    @PostMapping("/recuperar/reset")
    public ResponseEntity<?> resetContrasena(@Valid @RequestBody ResetContrasenaDTO dto) {
        boolean ok = usuarioService.resetContrasena(dto.getCorreo(), dto.getCodigo(), dto.getNuevaContrasena());
        if (ok) {
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña restablecida correctamente."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", "Código inválido o expirado."));
        }
    }

    /**
     * Cambia la contraseña del usuario actualmente autenticado (guardado en la sesión).
     *
     * <p>Requiere que el usuario esté presente en la sesión bajo el atributo "usuarioLogueado". Si no hay
     * usuario en sesión devuelve 401 UNAUTHORIZED. Si la contraseña actual no coincide, devuelve 400 BAD REQUEST.</p>
     *
     * @param dto DTO con la contraseña actual y la nueva contraseña. Validado por {@code @Valid}.
     * @param session HttpSession donde se busca el atributo "usuarioLogueado".
     * @return 200 OK si el cambio fue exitoso; 401 si no hay sesión; 400 si la contraseña actual es incorrecta.
     */
    @PostMapping("/cambiar")
    public ResponseEntity<?> cambiarContrasena(@Valid @RequestBody CambiarContrasenaDTO dto, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje", "Debe iniciar sesión para cambiar la contraseña."));
        }

        boolean cambiado = usuarioService.cambiarContrasena(usuario.getId_usuario(), dto.getContrasenaActual(), dto.getNuevaContrasena());
        if (cambiado) {
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña cambiada correctamente."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", "La contraseña actual es incorrecta."));
        }
    }

    /**
     * Devuelve el perfil del usuario actualmente en sesión.
     *
     * @param session HttpSession donde se busca el usuario bajo "usuarioLogueado".
     * @return 200 OK con la entidad Usuario si está en sesión; 401 UNAUTHORIZED si no hay sesión activa.
     */
    @GetMapping("/perfil")
    public ResponseEntity<?> verPerfil(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "mensaje", "Debe iniciar sesión para ver su perfil."
            ));
        }
    }

    /**
     * Cierra la sesión HTTP del usuario actual invalidando la sesión.
     *
     * @param session HttpSession a invalidar.
     * @return 200 OK con mensaje de confirmación.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> cerrarSesion(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of(
                "mensaje", "Sesión cerrada correctamente."
        ));
    }
}