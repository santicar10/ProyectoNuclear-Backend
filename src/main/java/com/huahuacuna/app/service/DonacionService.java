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

/**
 * Servicio que encapsula la lógica de negocio relacionada con las donaciones.
 *
 * <p>Responsabilidades principales:</p>
 * <ul>
 *   <li>Crear y persistir donaciones a partir de DTOs.</li>
 *   <li>Operaciones de lectura (listar, filtrar, obtener por id).</li>
 *   <li>Actualizar estado de una donación y eliminar donaciones.</li>
 *   <li>Construir reportes de donantes y generar un CSV descargable.</li>
 * </ul>
 *
 * <p>Notas importantes:</p>
 * <ul>
 *   <li>Al crear una donación, si no se especifica el tipo, se infiere automáticamente:
 *       MONETARIA si existe un monto &gt; 0; MATERIAL si existe tipoDotacion no vacío.</li>
 *   <li>Se utiliza el repositorio {@link DonacionRepository} para acceso a datos.</li>
 *   <li>Los métodos que modifican el estado de datos están anotados con {@code @Transactional}.</li>
 * </ul>
 *
 * @see DonacionRepository
 * @see DonacionDTO
 * @see DonorReportDTO
 * @author Janka033
 * @since 1.0
 */
@Service
public class DonacionService {

    /**
     * Repositorio para operaciones CRUD sobre donaciones.
     * Inyectado por Spring.
     */
    @Autowired
    private DonacionRepository donacionRepository;

    /**
     * Crea y persiste una nueva donación a partir de la información proporcionada en el DTO.
     *
     * <p>Comportamiento específico:</p>
     * <ul>
     *   <li>Si {@code dto.idUsuario} es nulo, se asigna por defecto {@code 1L} como id de usuario.</li>
     *   <li>Si {@code dto.tipo} está presente se usa tal cual; en caso contrario se infiere:
     *       - MONETARIA si {@code dto.monto} &gt; 0
     *       - MATERIAL si {@code dto.tipoDotacion} no es nulo ni vacío</li>
     *   <li>Si no se puede inferir el tipo lanza {@link IllegalArgumentException}.</li>
     * </ul>
     *
     * @param dto DTO con los datos de la donación (monto, tipo, tipoDotacion, etc.). Debe contener
     *            la información necesaria para crear la entidad.
     * @return La entidad {@link Donacion} persistida.
     * @throws IllegalArgumentException si no es posible determinar el tipo de donación (monetaria o material).
     */
    @Transactional
    public Donacion crearDonacion(DonacionDTO dto) {
        Donacion donacion = new Donacion();

        //  Asignar usuario solo si viene en el DTO
        donacion.setIdUsuario(dto.getIdUsuario() != null ? dto.getIdUsuario() : 1L);

        //  Inferir tipo automáticamente si no viene
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

    /**
     * Devuelve todas las donaciones almacenadas.
     *
     * @return Lista de {@link Donacion}.
     */
    public List<Donacion> listarTodas() {
        return donacionRepository.findAll();
    }

    /**
     * Lista donaciones filtradas por su estado.
     *
     * @param estado Estado de donación a filtrar.
     * @return Lista de donaciones en el estado indicado.
     */
    public List<Donacion> listarPorEstado(Donacion.EstadoDonacion estado) {
        return donacionRepository.findByEstado(estado);
    }

    /**
     * Lista donaciones asociadas a un correo electrónico.
     *
     * @param correo Correo electrónico del donante.
     * @return Lista de donaciones encontradas para el correo.
     */
    public List<Donacion> listarPorCorreo(String correo) {
        return donacionRepository.findByCorreoElectronico(correo);
    }

    /**
     * Obtiene una donación por su identificador.
     *
     * @param id Identificador de la donación.
     * @return Donación encontrada.
     * @throws RuntimeException si no se encuentra la donación (mensaje: "Donación no encontrada").
     */
    public Donacion obtenerPorId(Long id) {
        return donacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donación no encontrada"));
    }

    /**
     * Actualiza el estado de una donación y la persiste.
     *
     * @param id         Identificador de la donación a actualizar.
     * @param nuevoEstado Nuevo estado a asignar.
     * @return La entidad {@link Donacion} actualizada.
     */
    @Transactional
    public Donacion actualizarEstado(Long id, Donacion.EstadoDonacion nuevoEstado) {
        Donacion donacion = obtenerPorId(id);
        donacion.setEstado(nuevoEstado);
        return donacionRepository.save(donacion);
    }

    /**
     * Elimina una donación por su id.
     *
     * @param id Identificador de la donación a eliminar.
     */
    @Transactional
    public void eliminar(Long id) {
        donacionRepository.deleteById(id);
    }

    // ----------------- REPORTS -----------------

    /**
     * Construye una lista de {@link DonorReportDTO} aplicando los filtros indicados.
     *
     * <p>Este método transforma las filas retornadas por la consulta del repositorio (Object[])
     * a DTOs tipados, manejando valores nulos y distintos tipos de timestamp que puedan venir
     * desde la base de datos (java.sql.Timestamp, java.util.Date o cadenas parseables).</p>
     *
     * @param from        Fecha/hora inicial del filtro (inclusive). Puede ser {@code null}.
     * @param to          Fecha/hora final del filtro (inclusive). Puede ser {@code null}.
     * @param tipo        Tipo de donación a filtrar (por ejemplo "MONETARIA" o "MATERIAL"). Puede ser {@code null}.
     * @param tipoDotacion Subtipo/descripcion de dotación (ej. "Alimentos"). Puede ser {@code null}.
     * @return Lista de {@link DonorReportDTO} con idUsuario, correo, totalDonado, totalDonaciones y fecha de última donación.
     */
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

    /**
     * Genera un CSV en memoria con el reporte de donantes aplicando los filtros indicados.
     *
     * <p>El CSV incluye un BOM UTF-8 al inicio para mejorar la compatibilidad con Excel y define
     * las columnas: idUsuario, correoElectronico, totalDonado, totalDonaciones, ultimaDonacion.</p>
     *
     * @param from        Fecha/hora inicial del filtro (inclusive). Puede ser {@code null}.
     * @param to          Fecha/hora final del filtro (inclusive). Puede ser {@code null}.
     * @param tipo        Tipo de donación a filtrar (por ejemplo "MONETARIA" o "MATERIAL"). Puede ser {@code null}.
     * @param tipoDotacion Subtipo/descripcion de dotación (ej. "Alimentos"). Puede ser {@code null}.
     * @return Array de bytes con el contenido CSV codificado en UTF-8.
     * @throws UnsupportedEncodingException declaración mantenida por compatibilidad (actualmente no usada
     *                                      por la implementación, ya que se convierte a bytes con UTF-8).
     */
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

    /**
     * Escapa valores para CSV: dobla comillas internas y rodea con comillas si es necesario.
     *
     * @param value Texto de entrada.
     * @return Valor seguro para colocar dentro de una celda CSV.
     */
    private String escapeCsv(String value) {
        if (value == null) return "";
        String s = value.replace("\"", "\"\"");
        if (s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r")) {
            return "\"" + s + "\"";
        }
        return s;
    }
}