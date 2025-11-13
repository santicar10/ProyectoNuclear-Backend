package com.huahuacuna.app.controller;

import com.huahuacuna.app.DTO.DonacionDTO;
import com.huahuacuna.app.model.Donacion;
import com.huahuacuna.app.service.DonacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para operaciones relacionadas con donaciones.
 *
 * <p>Expone endpoints para crear, listar, obtener, actualizar estado,
 * eliminar donaciones y descargar reportes (CSV) de donantes.</p>
 *
 * Rutas:
 * - POST   /api/donaciones           : crear una donación
 * - GET    /api/donaciones           : listar todas las donaciones
 * - GET    /api/donaciones/estado/{estado} : listar por estado
 * - GET    /api/donaciones/correo/{correo} : listar por correo
 * - GET    /api/donaciones/{id}      : obtener donación por id
 * - PATCH  /api/donaciones/{id}/estado : actualizar estado
 * - DELETE /api/donaciones/{id}      : eliminar donación
 * - GET    /api/donaciones/reporte   : descargar CSV de donantes con filtros
 *
 * @author Janka033
 * @since 1.0
 */
@RestController
@RequestMapping("/api/donaciones")
@CrossOrigin(origins = "*")
public class DonacionController {

    /**
     * Servicio que contiene la lógica de negocio para donaciones.
     * Inyectado por Spring.
     */
    @Autowired
    private DonacionService donacionService;

    /**
     * Crea una donación a partir de un DTO válido.
     *
     * @param dto DTO con los datos de la donación. Se valida con {@code @Valid}.
     * @return ResponseEntity con la donación creada y código HTTP 201 (CREATED).
     */
    @PostMapping
    public ResponseEntity<Donacion> crear(@Valid @RequestBody DonacionDTO dto) {
        Donacion creada = donacionService.crearDonacion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    /**
     * Lista todas las donaciones existentes.
     *
     * @return ResponseEntity con la lista de donaciones y código HTTP 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<Donacion>> listarTodas() {
        return ResponseEntity.ok(donacionService.listarTodas());
    }

    /**
     * Lista donaciones filtradas por estado.
     *
     * @param estado Estado de la donación (ej. PENDIENTE, ACEPTADA, RECHAZADA) definido en {@link Donacion.EstadoDonacion}.
     * @return ResponseEntity con la lista de donaciones que cumplen el estado y código HTTP 200 (OK).
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Donacion>> listarPorEstado(@PathVariable Donacion.EstadoDonacion estado) {
        return ResponseEntity.ok(donacionService.listarPorEstado(estado));
    }

    /**
     * Lista donaciones asociadas a un correo electrónico.
     *
     * @param correo Correo del donante a filtrar.
     * @return ResponseEntity con la lista de donaciones del correo dado y código HTTP 200 (OK).
     */
    @GetMapping("/correo/{correo}")
    public ResponseEntity<List<Donacion>> listarPorCorreo(@PathVariable String correo) {
        return ResponseEntity.ok(donacionService.listarPorCorreo(correo));
    }

    /**
     * Obtiene una donación por su identificador.
     *
     * @param id Identificador único de la donación.
     * @return ResponseEntity con la donación encontrada y código HTTP 200 (OK).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Donacion> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(donacionService.obtenerPorId(id));
    }

    /**
     * Actualiza el estado de una donación.
     *
     * <p>Ejemplo de uso: PATCH /api/donaciones/123/estado?estado=ACEPTADA</p>
     *
     * @param id     Identificador de la donación a actualizar.
     * @param estado Nuevo estado para la donación (enum {@link Donacion.EstadoDonacion}).
     * @return ResponseEntity con la donación actualizada y código HTTP 200 (OK).
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Donacion> actualizarEstado(
            @PathVariable Long id,
            @RequestParam Donacion.EstadoDonacion estado) {
        return ResponseEntity.ok(donacionService.actualizarEstado(id, estado));
    }

    /**
     * Elimina una donación por su id.
     *
     * @param id Identificador de la donación a eliminar.
     * @return ResponseEntity vacío con código HTTP 204 (NO CONTENT) si la eliminación fue exitosa.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        donacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Descarga un informe CSV de donantes aplicando filtros opcionales.
     *
     * <p>Parámetros de consulta soportados:</p>
     * <ul>
     *     <li>from (LocalDateTime, formato ISO) - fecha/hora desde</li>
     *     <li>to   (LocalDateTime, formato ISO) - fecha/hora hasta</li>
     *     <li>tipo (String) - tipo de donación: por ejemplo "MONETARIA" o "MATERIAL"</li>
     *     <li>tipoDotacion (String) - subcategoría o descripción del tipo (ej. "Alimentos")</li>
     *     <li>format (String) - formato de salida; actualmente solo se acepta "csv" (valor por defecto "csv")</li>
     * </ul>
     *
     * <p>Ejemplos:</p>
     * <pre>
     * GET /api/donaciones/reporte?tipo=MONETARIA
     * GET /api/donaciones/reporte?tipo=MATERIAL&tipoDotacion=Alimentos&from=2025-01-01T00:00:00
     * </pre>
     *
     * @param from        Fecha/hora inicial (opcional). Formato ISO-8601 (p. ej. 2025-01-01T10:00:00).
     * @param to          Fecha/hora final (opcional). Formato ISO-8601.
     * @param tipo        Tipo de donación (opcional).
     * @param tipoDotacion Subtipo o descripción de la dotación (opcional).
     * @param format      Formato de salida (por defecto "csv"). Actualmente solo se acepta "csv".
     * @return ResponseEntity con el recurso byte (CSV) y cabeceras para forzar descarga. Código HTTP 200 (OK) si todo va bien,
     *         HTTP 400 (BAD REQUEST) si se solicita un formato no soportado.
     * @throws UnsupportedEncodingException Si ocurre un problema construyendo el nombre del archivo (raramente).
     */
    @GetMapping("/reporte")
    public ResponseEntity<ByteArrayResource> descargarReporteDonantes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String tipoDotacion,
            @RequestParam(required = false, defaultValue = "csv") String format
    ) throws UnsupportedEncodingException {

        if (!"csv".equalsIgnoreCase(format)) {
            return ResponseEntity.badRequest().build();
        }

        byte[] data = donacionService.generateDonorReportCsv(from, to, tipo, tipoDotacion);
        ByteArrayResource resource = new ByteArrayResource(data);

        String filename = "reporte_donantes";
        if (tipo != null) filename += "_" + tipo;
        if (tipoDotacion != null) filename += "_" + tipoDotacion.replace(" ", "_");
        filename += ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentLength(data.length)
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(resource);
    }
}