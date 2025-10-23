package com.proyectonuclear.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal que arranca la aplicación Spring Boot para el módulo ProyectoNuclear-backend.
 *
 * Responsabilidades:
 * - Inicializar el contexto de Spring Boot.
 * - Iniciar el servidor embebido (Tomcat) y exponer los controladores definidos en el proyecto.
 *
 * Ejecución:
 * - Desde IDE: ejecutar la clase `ProyectoNuclearBackendApplication`.
 * - Desde línea de comandos:
 *     ./mvnw spring-boot:run
 *   o
 *     java -jar target/<artifact>.jar
 */
@SpringBootApplication
public class ProyectoNuclearBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoNuclearBackendApplication.class, args);
	}

}