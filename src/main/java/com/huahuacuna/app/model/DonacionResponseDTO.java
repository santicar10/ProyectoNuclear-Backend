package com.huahuacuna.app.model;

import com.huahuacuna.app.model.Donacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonacionResponseDTO {
    private Long id;
    private Long idUsuario;  // Solo se muestra si existe
    private Donacion.TipoDonacion tipo;
    private BigDecimal monto;
    private String descripcion;
    private Donacion.EstadoDonacion estado;
    private LocalDateTime fechaDonacion;

    // Excluir datos sensibles como banco, nit, correo en listados públicos
}