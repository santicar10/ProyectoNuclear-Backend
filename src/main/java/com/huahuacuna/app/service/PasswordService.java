package com.huahuacuna.app.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * Servicio para generar contraseñas aleatorias.
 *
 * <p>Proporciona dos métodos públicos:
 * - generarContrasenaAleatoria(int length): genera una contraseña con la longitud indicada.
 * - generarContrasenaAleatoria(): genera una contraseña con longitud por defecto (8).</p>
 *
 * Notas:
 * - Este servicio solo genera contraseñas en texto plano. Cuando se persistan en la base
 *   de datos, siempre deben guardarse como hashes seguros (BCrypt, Argon2, etc.).
 * - El conjunto de caracteres actual incluye letras mayúsculas, minúsculas y dígitos.
 *   Si se desea mayor entropía, añadir símbolos especiales o aumentar la longitud.
 */
@Service
public class PasswordService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Genera una contraseña aleatoria con los caracteres definidos en {@link #CHARACTERS}.
     *
     * @param length longitud deseada de la contraseña (debe ser > 0)
     * @return contraseña aleatoria como {@code String}
     * @throws IllegalArgumentException si {@code length <= 0}
     */
    public String generarContrasenaAleatoria(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("La longitud de la contraseña debe ser mayor que 0");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    /**
     * Genera una contraseña aleatoria con longitud por defecto (8).
     *
     * @return contraseña aleatoria de 8 caracteres
     */
    public String generarContrasenaAleatoria() {
        return generarContrasenaAleatoria(8);
    }
}