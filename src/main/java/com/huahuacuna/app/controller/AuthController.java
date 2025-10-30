package com.huahuacuna.app.controller;

import com.huahuacuna.app.DTO.LoginDTO;
import com.huahuacuna.app.model.Usuario;
import com.huahuacuna.app.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
/**
 * Controlador responsable de la autenticación básica (login) de usuarios.
 *
 * <p>
 * Exposición:
 * - POST /auth/login
 *   - Request body: {@link com.huahuacuna.app.DTO.LoginDTO} (correo, contrasena)
 *   - Respuestas:
 *     - 200 OK: mapa JSON con { "mensaje", "rol", "usuarioId" } cuando las credenciales son válidas.
 *     - 401 Unauthorized: mapa JSON con { "mensaje" : "Credenciales inválidas" } cuando no coinciden.
 * </p>
 *
 * Notas de seguridad y funcionamiento:
 * - Actualmente las contraseñas se comparan directamente con equals(...).
 *   Esto asume que las contraseñas están almacenadas exactamente como el usuario las envía.
 *   Recomendado: almacenar contraseñas hasheadas (BCrypt) y usar {@code BCrypt.checkpw}.
 * - El usuario autenticado se guarda en la sesión HTTP con la clave "usuarioLogueado".
 *   Dependiendo del flujo del proyecto, podría preferirse:
 *     - Usar tokens JWT (stateless) en vez de HttpSession.
 *     - Guardar únicamente el ID y rol en la sesión, no la entidad completa.
 * - Validar y sanitizar los datos de entrada (aquí se asume que LoginDTO ya tiene validaciones si son necesarias).
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {
    /**
     * Repositorio JPA para acceder a la entidad Usuario.
     *
     * Inyección por campo con @Autowired (se puede cambiar a inyección por constructor si se desea
     * favorecer testabilidad y evitar uso directo de @Autowired en campos).
     */
    @Autowired
    private UsuarioRepository usuarioRepository;
    /**
     * Endpoint para iniciar sesión.
     *
     * Flujo:
     * 1. Busca un usuario por correo usando {@link UsuarioRepository#findByCorreo(String)}.
     * 2. Si el usuario existe y la contraseña coincide, guarda el usuario en la sesión HTTP y
     *    responde 200 OK con información mínima (mensaje, rol y usuarioId).
     * 3. Si no coincide, responde 401 Unauthorized con un mensaje de credenciales inválidas.
     *
     * @param loginRequest DTO con campos "correo" y "contrasena".
     * @param session      HttpSession actual; se usa para almacenar el usuario logueado.
     * @return ResponseEntity con mapa JSON (éxito o error).
     */


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginRequest, HttpSession session) {
        Optional<Usuario> usuario = usuarioRepository.findByCorreo(loginRequest.getCorreo());

        if (usuario.isPresent() && usuario.get().getContrasena().equals(loginRequest.getContrasena())) {
            // Guardar en la sesión. Considerar guardar solo el ID/rol en vez del objeto completo.
            session.setAttribute("usuarioLogueado", usuario.get());

            // Respuesta simplificada con información útil para el cliente.
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Inicio de sesión exitoso",
                    "rol", usuario.get().getRol(),
                    "usuarioId", usuario.get().getId_usuario()
            ));
        } else {
            // No exponer detalles (por ejemplo, si el correo no existe o la contraseña está mal): mensaje genérico.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "mensaje", "Credenciales inválidas"
            ));
        }
    }
}
