package com.huahuacuna.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador minimalista usado como endpoint de prueba/health-check.
 *
 * <p>Expone:
 * <ul>
 *   <li>GET /hola — devuelve un emoticón ":D" para verificar que el servidor responde.</li>
 * </ul>
 *
 * <p>Notas:
 * - Ideal para smoke-tests. Para checks de salud más completos, considerar un endpoint que
 *   valide dependencias (BD, cola, etc.) o usar Spring Boot Actuator.
 */
@RestController
public class HelloController {

    @GetMapping("/hola")
    public String saludar() {
        return ":D";
    }
}