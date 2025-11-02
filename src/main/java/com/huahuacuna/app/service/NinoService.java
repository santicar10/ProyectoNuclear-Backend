package com.huahuacuna.app.service;


import com.huahuacuna.app.DTO.NinoDTO;
import com.huahuacuna.app.model.Nino;
import com.huahuacuna.app.repository.NinoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para la gestión de niños (CRUD).
 */
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
        Nino nino = new Nino();
        nino.setNombre(dto.getNombre());
        nino.setEdad(dto.getEdad());
        nino.setGenero(dto.getGenero());
        nino.setDescripcion(dto.getDescripcion());
        nino.setFotoUrl(dto.getFotoUrl());
        nino.setEstadoApadrinamiento(dto.getEstadoApadrinamiento());
        nino.setFechaRegistro(dto.getFechaRegistro() != null ? dto.getFechaRegistro() : nino.getFechaRegistro());
        return ninoRepository.save(nino);
    }

    public Nino actualizarParcial(Integer id, Map<String, Object> campos) {
        Nino nino = ninoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Niño no encontrado."));

        campos.forEach((clave, valor) -> {
            switch (clave) {
                case "nombre" -> nino.setNombre((String) valor);
                case "edad" -> nino.setEdad((Integer) valor);
                case "genero" -> nino.setGenero((String) valor);
                case "descripcion" -> nino.setDescripcion((String) valor);
                case "foto_url" -> nino.setFotoUrl((String) valor) ;
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

