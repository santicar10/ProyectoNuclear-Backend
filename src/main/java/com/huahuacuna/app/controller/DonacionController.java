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

@RestController
@RequestMapping("/api/donaciones")
@CrossOrigin(origins = "*")
public class DonacionController {

    @Autowired
    private DonacionService donacionService;

    @PostMapping
    public ResponseEntity<Donacion> crear(@Valid @RequestBody DonacionDTO dto) {
        Donacion creada = donacionService.crearDonacion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping
    public ResponseEntity<List<Donacion>> listarTodas() {
        return ResponseEntity.ok(donacionService.listarTodas());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Donacion>> listarPorEstado(@PathVariable Donacion.EstadoDonacion estado) {
        return ResponseEntity.ok(donacionService.listarPorEstado(estado));
    }

    @GetMapping("/correo/{correo}")
    public ResponseEntity<List<Donacion>> listarPorCorreo(@PathVariable String correo) {
        return ResponseEntity.ok(donacionService.listarPorCorreo(correo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Donacion> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(donacionService.obtenerPorId(id));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Donacion> actualizarEstado(
            @PathVariable Long id,
            @RequestParam Donacion.EstadoDonacion estado) {
        return ResponseEntity.ok(donacionService.actualizarEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        donacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Descargar informe de donantes en CSV con filtros opcionales:
     * - from (ISO datetime)
     * - to   (ISO datetime)
     * - tipo (MONETARIA or MATERIAL)
     * - tipoDotacion (ej. "Alimentos")
     *
     * Ejemplo:
     * GET /api/donaciones/reporte?tipo=MONETARIA
     * GET /api/donaciones/reporte?tipo=MATERIAL&tipoDotacion=Alimentos
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