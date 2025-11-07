package com.huahuacuna.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un usuario de la aplicación.
 *
 * <p>Mapeada a la tabla "usuarios". Contiene información básica de un usuario:
 * identificador, nombre, correo, contraseña, datos de contacto, rol, fecha de
 * creación y estado.</p>
 *
 * <p>Notas importantes:
 * - La contraseña se almacena en el campo {@code contrasena}. Por seguridad, en
 *   producción este campo debe contener un hash (por ejemplo BCrypt). No guardar
 *   contraseñas en texto plano.
 * - Considerar añadir validaciones (@NotNull, @Email, @Size, etc.) ya sea aquí
 *   o a través de DTOs para la entrada (recomendado usar DTOs para los endpoints).
 * - La fecha de creación es un {@link LocalDate}; revisar la política de timezone
 *   si se necesita precisión horaria (usar {@link java.time.OffsetDateTime} o similar).
 * </p>
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class Usuario {

    /**
     * Identificador único del usuario (clave primaria, autogenerada).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_usuario;

    /**
     * Nombre completo del usuario. No puede ser nulo.
     */
    @Column(nullable = false)
    private String nombre;

    /**
     * Correo electrónico del usuario. Debe ser único y no nulo.
     */
    @Column(nullable = false, unique = true)
    private String correo;

    /**
     * Contraseña del usuario.
     *
     * <p>Nota: almacenar el hash de la contraseña, no la contraseña en texto plano.
     * Recomendado: {@code BCrypt.hashpw(password, BCrypt.gensalt())} al guardar y
     * {@code BCrypt.checkpw(rawPassword, storedHash)} al validar.</p>
     */
    @Column(nullable = false)
    private String contrasena;

    /**
     * Teléfono de contacto (opcional).
     */
    private String telefono;

    /**
     * Dirección física (opcional).
     */
    private String direccion;

    /**
     * Rol del usuario en la aplicación.
     *
     * <p>Se almacena como texto (EnumType.STRING) en la BD para legibilidad y
     * para evitar problemas si se reordena el enum.</p>
     */
    @Enumerated(EnumType.STRING)
    private Rol rol;

    /**
     * Fecha de creación del usuario (solo fecha).
     *
     * <p>Si se requiere la hora exacta, cambiar a {@code OffsetDateTime} o {@code Instant}.</p>
     */

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fecha_creacion = LocalDate.now();


    /**
     * Estado del usuario (activo / inactivo).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.activo;

    /**
     * -----------------------
     * Campos añadidos para recuperación de contraseña por código
     * -----------------------
     *
     * recoveryCode: código numérico de 6 dígitos enviado por email para la recuperación.
     * recoveryExpiry: fecha y hora de expiración del código (ej. ahora + 15 minutos).
     *
     * Ambos campos son nullable en la BD y se deben limpiar después de usar el código.
     */
    @Column(name = "recovery_code", nullable = true)
    private String recoveryCode;

    @Column(name = "recovery_expiry", nullable = true)
    private LocalDateTime recoveryExpiry;

    /**
     * Roles posibles que puede tener un usuario en el sistema.
     *
     * <p>Valores actuales:
     * - {@code administrador}
     * - {@code voluntario}
     * - {@code padrino}</p>
     */
    public enum Rol {
        administrador, voluntario, padrino
    }

    /**
     * Estados posibles para la entidad Usuario.
     *
     * <p>Valores actuales:
     * - {@code activo}
     * - {@code inactivo}</p>
     */
    public enum Estado {
        activo, inactivo
    }
}