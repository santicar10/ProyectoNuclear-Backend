package com.huahuacuna.app.DTO;

import com.huahuacuna.app.model.Donacion;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonacionDTO {

    private Long idUsuario;  // Opcional - solo si hay usuario autenticado

    private Donacion.TipoDonacion tipo;  // ⭐ OPCIONAL - se inferirá automáticamente

    private BigDecimal monto;  // Para donaciones monetarias

    private String descripcion;

    private String banco;

    @Email(message = "El correo electrónico debe ser válido")
    private String correoElectronico;

    private String nit;

    private String tipoDotacion;  // Para donaciones materiales
}