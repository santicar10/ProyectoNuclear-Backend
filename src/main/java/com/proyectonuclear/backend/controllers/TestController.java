package com.proyectonuclear.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador mínimo para comprobar que la aplicación responde correctamente.
 *
 * Endpoints:
 * - GET /  -> devuelve un mensaje simple indicando que la aplicación está funcionando.
 *
 * Uso:
 * - Este endpoint es útil como smoke-check o health-check básico durante desarrollo.
 * - Para comprobaciones más completas de salud, considerar integrar Spring Boot Actuator.
 */
@RestController
public class TestController {

    /**
     * Endpoint raíz que confirma el correcto despliegue y funcionamiento de la app.
     *
     * @return Mensaje simple en texto plano.
     */
    @GetMapping("/")
    public String home() {
        return "¡Aplicación funcionando!";
    }
}