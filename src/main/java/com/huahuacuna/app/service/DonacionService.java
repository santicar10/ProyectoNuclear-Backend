package com.huahuacuna.app.service;

import com.huahuacuna.app.DTO.DonacionDTO;
import com.huahuacuna.app.DTO.DonorReportDTO;
import com.huahuacuna.app.model.Donacion;
import com.huahuacuna.app.repository.DonacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class DonacionService {

    @Autowired
    private DonacionRepository donacionRepository;

    @Transactional
    public Donacion crearDonacion(DonacionDTO dto) {
        Donacion donacion = new Donacion();

        // ⭐ Asignar usuario solo si viene en el DTO
        donacion.setIdUsuario(dto.getIdUsuario() != null ? dto.getIdUsuario() : 1L);

        // ⭐ Inferir tipo automáticamente si no viene
        if (dto.getTipo() != null) {
            donacion.setTipo(dto.getTipo());
        } else {
            // Si hay monto, es MONETARIA; si hay tipoDotacion, es MATERIAL
            if (dto.getMonto() != null && dto.getMonto().compareTo(java.math.BigDecimal.ZERO) > 0) {
                donacion.setTipo(Donacion.TipoDonacion.MONETARIA);
            } else if (dto.getTipoDotacion() != null && !dto.getTipoDotacion().isEmpty()) {
                donacion.setTipo(Donacion.TipoDonacion.MATERIAL);
            } else {
                throw new IllegalArgumentException("Debe especificar un monto (MONETARIA) o tipo de dotación (MATERIAL)");
            }
        }

        donacion.setMonto(dto.getMonto());
        donacion.setDescripcion(dto.getDescripcion());
        donacion.setBanco(dto.getBanco());
        donacion.setCorreoElectronico(dto.getCorreoElectronico());
        donacion.setNit(dto.getNit());
        donacion.setTipoDotacion(dto.getTipoDotacion());

        return donacionRepository.save(donacion);
    }

    public List<Donacion> listarTodas() {
        return donacionRepository.findAll();
    }

    public List<Donacion> listarPorEstado(Donacion.EstadoDonacion estado) {
        return donacionRepository.findByEstado(estado);
    }

    public List<Donacion> listarPorCorreo(String correo) {
        return donacionRepository.findByCorreoElectronico(correo);
    }

    public Donacion obtenerPorId(Long id) {
        return donacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donación no encontrada"));
    }

    @Transactional
    public Donacion actualizarEstado(Long id, Donacion.EstadoDonacion nuevoEstado) {
        Donacion donacion = obtenerPorId(id);
        donacion.setEstado(nuevoEstado);
        return donacionRepository.save(donacion);
    }

    @Transactional
    public void eliminar(Long id) {
        donacionRepository.deleteById(id);
    }
    // ----------------- REPORTS -----------------
// Actualizado para aceptar filtros tipo y tipoDotacion
    public List<DonorReportDTO> buildDonorReport(LocalDateTime from, LocalDateTime to, String tipo, String tipoDotacion) {
        List<Object[]> rows = donacionRepository.findDonorReport(from, to, tipo, tipoDotacion);
        List<DonorReportDTO> report = new ArrayList<>();
        for (Object[] r : rows) {
            Long idUsuario = r[0] == null ? 0L : ((Number) r[0]).longValue();
            String correo = r[1] == null ? "" : r[1].toString();
            BigDecimal totalDonado = r[2] == null ? BigDecimal.ZERO : new BigDecimal(r[2].toString());
            Long totalDonaciones = r[3] == null ? 0L : ((Number) r[3]).longValue();
            LocalDateTime ultima = null;
            if (r[4] != null) {
                Object ts = r[4];
                if (ts instanceof java.sql.Timestamp) {
                    ultima = ((java.sql.Timestamp) ts).toLocalDateTime();
                } else if (ts instanceof java.util.Date) {
                    ultima = ((java.util.Date) ts).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                } else {
                    try { ultima = LocalDateTime.parse(ts.toString()); } catch (Exception ignored) {}
                }
            }
            report.add(new DonorReportDTO(idUsuario, correo, totalDonado, totalDonaciones, ultima));
        }
        return report;
    }

    public byte[] generateDonorReportCsv(LocalDateTime from, LocalDateTime to, String tipo, String tipoDotacion) throws UnsupportedEncodingException {
        List<DonorReportDTO> rows = buildDonorReport(from, to, tipo, tipoDotacion);
        StringBuilder sb = new StringBuilder();
        final String bom = "\uFEFF";
        sb.append(bom);
        sb.append("idUsuario,correoElectronico,totalDonado,totalDonaciones,ultimaDonacion\n");
        for (DonorReportDTO r : rows) {
            sb.append(r.getIdUsuario() != null ? r.getIdUsuario() : 0).append(",");
            sb.append(escapeCsv(r.getCorreoElectronico())).append(",");
            sb.append(r.getTotalDonado() != null ? r.getTotalDonado().toPlainString() : "0").append(",");
            sb.append(r.getTotalDonaciones() != null ? r.getTotalDonaciones() : 0).append(",");
            sb.append(r.getUltimaDonacion() != null ? r.getUltimaDonacion().toString() : "").append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        String s = value.replace("\"", "\"\"");
        if (s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r")) {
            return "\"" + s + "\"";
        }
        return s;
    }
}