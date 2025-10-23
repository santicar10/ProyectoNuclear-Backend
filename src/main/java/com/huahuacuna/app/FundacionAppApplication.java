package com.huahuacuna.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación Spring Boot.
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Arrancar el contexto de Spring y la configuración automática.</li>
 *   <li>Iniciar el servidor embebido (Tomcat) y exponer los endpoints.</li>
 * </ul>
 *
 * <p>Notas operativas:
 * <ul>
 *   <li>Configurar propiedades en src/main/resources/application.properties (puerto, datasource, perfiles).</li>
 *   <li>Para desarrollo, puede usarse Spring DevTools (reinicio automático).</li>
 *   <li>Arranque desde línea de comandos: <code>./mvnw spring-boot:run</code> o ejecutar el jar empaquetado.</li>
 * </ul>
 */
@SpringBootApplication
public class FundacionAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(FundacionAppApplication.class, args);
	}

}