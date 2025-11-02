package com.huahuacuna.app.controller;

import com.huahuacuna.app.DTO.RegistroVoluntariadoDTO;
import com.huahuacuna.app.model.Voluntariado;
import com.huahuacuna.app.service.VoluntariadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/voluntariado")
public class VoluntariadoController {

    @Autowired
    private VoluntariadoService voluntariadoService;

    /**
     * Endpoint público para registrarse como voluntario.
     */
    @PostMapping("/registro")
    public ResponseEntity<Voluntariado> registrarVoluntario(@RequestBody RegistroVoluntariadoDTO dto) {
        Voluntariado nuevo = voluntariadoService.registrarVoluntario(dto);
        return ResponseEntity.ok(nuevo);
    }
}

