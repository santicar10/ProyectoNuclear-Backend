package com.huahuacuna.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "donaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Donacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_donacion")
    private Long id;

    @Column(name = "id_usuario", nullable = true)
    private Long idUsuario;

    @NotNull(message = "El tipo de donación es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoDonacion tipo;

    @Column(name = "monto")
    private BigDecimal monto;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @CreationTimestamp
    @Column(name = "fecha_donacion", updatable = false)
    private LocalDateTime fechaDonacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoDonacion estado = EstadoDonacion.PENDIENTE;

    @Column(name = "banco")
    private String banco;

    @Email(message = "El correo electrónico debe ser válido")
    @Column(name = "correo_electronico")
    private String correoElectronico;

    @Column(name = "nit")
    private String nit;

    @Column(name = "tipo_dotacion")
    private String tipoDotacion;

    // Enums
    public enum TipoDonacion {
        MONETARIA, MATERIAL
    }

    public enum EstadoDonacion {
        PENDIENTE, COMPLETADA, CANCELADA
    }
}