package com.huahuacuna.app.service;

import com.huahuacuna.app.DTO.ApadrinamientoDTO;
import com.huahuacuna.app.model.Apadrinamiento;
import com.huahuacuna.app.model.Apadrinamiento.EstadoApadrinamiento;
import com.huahuacuna.app.model.Nino;
import com.huahuacuna.app.model.Usuario;
import com.huahuacuna.app.repository.ApadrinamientoRepository;
import com.huahuacuna.app.repository.NinoRepository;
import com.huahuacuna.app.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ApadrinamientoService {

    @Autowired
    private ApadrinamientoRepository apadrinamientoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NinoRepository ninoRepository;

    @Transactional
    public ApadrinamientoDTO crearApadrinamiento(Integer idPadrino, Integer idNino) {
        Usuario padrino = usuarioRepository.findById(idPadrino)
                .orElseThrow(() -> new IllegalArgumentException("Padrino no encontrado"));

        if (padrino.getRol() != Usuario.Rol.padrino) {
            throw new IllegalArgumentException("El usuario no tiene rol de padrino");
        }

        Nino nino = ninoRepository.findById(idNino)
                .orElseThrow(() -> new IllegalArgumentException("Niño no encontrado"));

        if (nino.getEstadoApadrinamiento() != Nino.EstadoApadrinamiento.Disponible) {
            throw new IllegalArgumentException("El niño ya está apadrinado o no está disponible");
        }

        Optional<Apadrinamiento> existente = apadrinamientoRepository
                .findByNinoIdAndEstado(idNino, EstadoApadrinamiento.Activo);
        if (existente.isPresent()) {
            throw new IllegalArgumentException("El niño ya tiene un apadrinamiento activo");
        }

        Apadrinamiento apadrinamiento = new Apadrinamiento();
        apadrinamiento.setPadrino(padrino);
        apadrinamiento.setNino(nino);
        apadrinamiento.setFechaInicio(LocalDate.now());
        apadrinamiento.setEstado(EstadoApadrinamiento.Activo);

        nino.setEstadoApadrinamiento(Nino.EstadoApadrinamiento.Apadrinado);
        ninoRepository.save(nino);

        Apadrinamiento guardado = apadrinamientoRepository.save(apadrinamiento);
        return convertirADTO(guardado);
    }

    public List<ApadrinamientoDTO> obtenerApadrinamientosPorPadrino(Integer idPadrino) {
        List<Apadrinamiento> apadrinamientos = apadrinamientoRepository
                .findByPadrinoIdAndEstado(idPadrino, EstadoApadrinamiento.Activo);
        
        return apadrinamientos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<ApadrinamientoDTO> obtenerTodosApadrinamientosPorPadrino(Integer idPadrino) {
        List<Apadrinamiento> apadrinamientos = apadrinamientoRepository
                .findByPadrinoId(idPadrino);
        
        return apadrinamientos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ApadrinamientoDTO finalizarApadrinamiento(Integer idApadrinamiento) {
        Apadrinamiento apadrinamiento = apadrinamientoRepository.findById(idApadrinamiento)
                .orElseThrow(() -> new IllegalArgumentException("Apadrinamiento no encontrado"));

        apadrinamiento.setEstado(EstadoApadrinamiento.Finalizado);
        apadrinamiento.setFechaFin(LocalDate.now());

        Nino nino = apadrinamiento.getNino();
        nino.setEstadoApadrinamiento(Nino.EstadoApadrinamiento.Disponible);
        ninoRepository.save(nino);

        Apadrinamiento guardado = apadrinamientoRepository.save(apadrinamiento);
        return convertirADTO(guardado);
    }

    public boolean existeApadrinamiento(Integer idPadrino, Integer idNino) {
        Optional<Apadrinamiento> existente = apadrinamientoRepository
                .findByPadrinoIdAndNinoId(idPadrino, idNino);
        return existente.isPresent() && existente.get().getEstado() == EstadoApadrinamiento.Activo;
    }

    private ApadrinamientoDTO convertirADTO(Apadrinamiento apadrinamiento) {
        ApadrinamientoDTO dto = new ApadrinamientoDTO();
        dto.setIdApadrinamiento(apadrinamiento.getIdApadrinamiento());
        dto.setIdPadrino(apadrinamiento.getPadrino().getId_usuario());
        dto.setIdNino(apadrinamiento.getNino().getId_nino());
        dto.setFechaInicio(apadrinamiento.getFechaInicio());
        dto.setFechaFin(apadrinamiento.getFechaFin());
        dto.setEstado(apadrinamiento.getEstado());
        
        dto.setNombrePadrino(apadrinamiento.getPadrino().getNombre());
        
        Nino nino = apadrinamiento.getNino();
        dto.setNombreNino(nino.getNombre());
        dto.setFotoNino(nino.getFotoUrl());
        dto.setEdadNino(nino.getEdad());
        dto.setGeneroNino(nino.getGenero());
        dto.setDescripcionNino(nino.getDescripcion());
        
        return dto;
    }
}