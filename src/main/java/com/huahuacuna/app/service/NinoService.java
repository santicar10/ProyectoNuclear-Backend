package com.huahuacuna.app.service;

import com.huahuacuna.app.DTO.NinoDTO;
import com.huahuacuna.app.model.Nino;
import com.huahuacuna.app.repository.NinoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class NinoService {

    @Autowired
    private NinoRepository ninoRepository;

    public List<Nino> listarTodos() {
        return ninoRepository.findAll();
    }

    public Optional<Nino> buscarPorId(Integer id) {
        return ninoRepository.findById(id);
    }

    public Nino guardar(NinoDTO dto) {
        // Validaciones
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        
        if (dto.getFechaNacimiento() == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria");
        }
        
        // Validar que la fecha no sea futura
        if (dto.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
        }
        
        int edad = Period.between(dto.getFechaNacimiento(), LocalDate.now()).getYears();
        
        if (edad > 18) {
            throw new IllegalArgumentException("El niño debe ser menor de 18 años");
        }
        
        if (dto.getGenero() == null || dto.getGenero().trim().isEmpty()) {
            throw new IllegalArgumentException("El género es obligatorio");
        }
        
        Nino nino = new Nino();
        nino.setNombre(dto.getNombre().trim());
        nino.setFechaNacimiento(dto.getFechaNacimiento()); // ✅ Solo guardar fecha
        nino.setGenero(dto.getGenero().toUpperCase());
        nino.setDescripcion(dto.getDescripcion() != null ? dto.getDescripcion().trim() : "");
        nino.setFotoUrl(dto.getFotoUrl() != null && !dto.getFotoUrl().trim().isEmpty() ? dto.getFotoUrl() : null);
        
        if (dto.getEstadoApadrinamiento() != null) {
            nino.setEstadoApadrinamiento(dto.getEstadoApadrinamiento());
        } else {
            nino.setEstadoApadrinamiento(Nino.EstadoApadrinamiento.Disponible);
        }
        
        return ninoRepository.save(nino);
    }

    public Nino actualizarParcial(Integer id, Map<String, Object> campos) {
        Nino nino = ninoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Niño no encontrado."));

        campos.forEach((clave, valor) -> {
            switch (clave) {
                case "nombre" -> nino.setNombre((String) valor);
                case "fecha_nacimiento" -> {
                    if (valor instanceof String) {
                        nino.setFechaNacimiento(LocalDate.parse((String) valor));
                    } else if (valor instanceof LocalDate) {
                        nino.setFechaNacimiento((LocalDate) valor);
                    }
                }
                case "genero" -> nino.setGenero((String) valor);
                case "descripcion" -> nino.setDescripcion((String) valor);
                case "foto_url" -> nino.setFotoUrl((String) valor);
                case "estado_apadrinamiento" -> nino.setEstadoApadrinamiento(Nino.EstadoApadrinamiento.valueOf((String) valor));
                default -> throw new IllegalArgumentException("Campo no permitido: " + clave);
            }
        });

        return ninoRepository.save(nino);
    }

    public void eliminar(Integer id) {
        if (ninoRepository.existsById(id)) {
            ninoRepository.deleteById(id);
        } else {
            throw new RuntimeException("Niño no encontrado con ID " + id);
        }
    }
}