package com.huahuacuna.app.controller;

import com.huahuacuna.app.DTO.RecuperarDTO;
import com.huahuacuna.app.DTO.RegistroDTO;
import com.huahuacuna.app.model.Usuario;
import com.huahuacuna.app.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para operaciones sobre usuarios.
 *
 * Endpoints expuestos (base: /api/usuarios):
 * - POST /registro   -> registrar nuevo usuario
 * - POST /recuperar  -> solicitar recuperación de contraseña (envía nueva contraseña por correo)
 * - GET  /perfil     -> ver datos del usuario logueado (usa HttpSession)
 * - POST /logout     -> cerrar sesión (invalidar HttpSession)
 *
 * Notas generales:
 * - Está habilitado CORS para todos los orígenes con {@code @CrossOrigin(origins = "*")} (ajustar para producción).
 * - Por simplicidad el controlador delega la lógica al {@link UsuarioService}.
 * - El control de autenticación se basa en HttpSession: se asume que en el login se guarda el atributo
 *   "usuarioLogueado" en la sesión. Esto implica un enfoque stateful; considerar JWT/stateless si se requiere escalabilidad.
 */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    /**
     * Servicio que contiene la lógica de negocio para operaciones de usuario.
     *
     * Nota: se usa inyección por campo (@Autowired). Para mejor testabilidad/claridad,
     * se recomienda inyección por constructor en proyectos grandes.
     */
    @Autowired
    private UsuarioService usuarioService;

    /**
     * Registra un nuevo usuario.
     *
     * Request body:
     *  - Usuario (como JSON) con los campos necesarios para la entidad.
     *
     * Respuesta:
     *  - Devuelve la entidad {@link Usuario} persistida.
     *
     * Consideraciones:
     *  - Validar campos de entrada (email, contrasena, etc.) antes de persistir.
     *  - Asegurarse de que la contraseña sea almacenada de forma segura (hasheada).
     *
     * @param usuario entidad Usuario recibida en el body
     * @return Usuario persistido (puede incluir id generado)
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroDTO dto) {
        // Normalizar correo
        String correo = dto.getCorreo().trim().toLowerCase();

        // Verificar existencia
        if (usuarioService.existsByCorreo(correo)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", "El correo ya está registrado."));
        }

        // Mapear dto -> entidad
        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setNombre(dto.getNombre().trim());
        // La contraseña se hasheará en el servicio antes de persistir
        usuario.setContrasena(dto.getContrasena());

        Usuario saved = usuarioService.registrar(usuario);

        // Responder con datos mínimos (no enviar contraseña)
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "usuarioId", saved.getId_usuario(),
                "correo", saved.getCorreo(),
                "nombre", saved.getNombre()
        ));
    }

    /**
     * Recupera la contraseña para el correo especificado en el DTO.
     *
     * Flujo esperado:
     *  - Llama a {@link UsuarioService#recuperarContrasena(String)}, que debería:
     *      - verificar existencia del correo,
     *      - generar una nueva contraseña segura o un token de recuperación,
     *      - enviar un correo al usuario con instrucciones o nueva contraseña,
     *      - devolver true si se envió correctamente, false si no se encontró el usuario.
     *
     * Respuesta:
     *  - Cadena con mensaje informativo (éxito o no encontrado).
     *
     * @param request DTO con el campo correo (RecuperarDTO)
     * @return mensaje informando el resultado de la operación
     */
    @PostMapping("/recuperar")
    public String recuperar(@RequestBody RecuperarDTO request) {
        boolean resultado = usuarioService.recuperarContrasena(request.getCorreo());
        if (resultado) {
            return "Se ha enviado una nueva contraseña a tu correo.";
        } else {
            return "No se encontró ningún usuario con ese correo.";
        }
    }

    /**
     * Devuelve el perfil del usuario que esté almacenado en la sesión HTTP bajo la clave "usuarioLogueado".
     *
     * Comportamiento:
     *  - Si existe un usuario en sesión devuelve la entidad {@link Usuario}.
     *  - Si no hay usuario en sesión devuelve un mensaje indicando que debe iniciar sesión.
     *
     * Notas:
     *  - Devolver la entidad completa puede exponer datos sensibles (p. ej. password). Considerar:
     *     - devolver un DTO de perfil (sin contraseña),
     *     - o limpiar campos sensibles antes de devolver.
     *
     * @param session HttpSession donde se busca el atributo "usuarioLogueado"
     * @return Usuario (si está en sesión) o mensaje (String) solicitando autenticación
     */
    @GetMapping("/perfil")
    public Object verPerfil(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario != null) {
            return usuario;
        } else {
            return "Debe iniciar sesión para ver su perfil.";
        }
    }

    /**
     * Cierra la sesión actual invalidando {@link HttpSession}.
     *
     * Respuesta:
     *  - Mensaje simple indicando que la sesión fue cerrada.
     *
     * @param session HttpSession actual que será invalidada
     * @return mensaje confirmando cierre de sesión
     */
    @PostMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "Sesión cerrada correctamente.";
    }

}