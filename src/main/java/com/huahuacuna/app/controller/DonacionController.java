package com.huahuacuna.app.controller;

import com.huahuacuna.app.DTO.DonacionDTO;
import com.huahuacuna.app.model.Donacion;
import com.huahuacuna.app.service.DonacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}