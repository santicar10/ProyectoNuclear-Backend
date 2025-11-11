package com.huahuacuna.app.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonorReportDTO {
    private Long idUsuario;            // 0 si anónimo/no definido
    private String correoElectronico;  // puede ser null o vacío
    private BigDecimal totalDonado;    // suma de montos (0 si no hay)
    private Long totalDonaciones;      // número total de donaciones (monetarias+materiales)
    private LocalDateTime ultimaDonacion;
}
