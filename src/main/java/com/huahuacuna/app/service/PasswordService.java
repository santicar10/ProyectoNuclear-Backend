package com.huahuacuna.app.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class PasswordService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public String generarContrasenaAleatoria(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public String generarContrasenaAleatoria() {
        return generarContrasenaAleatoria(8);
    }
}