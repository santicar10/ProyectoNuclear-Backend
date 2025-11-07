package com.huahuacuna.app.service;

import com.huahuacuna.app.DTO.EventoDTO;
import com.huahuacuna.app.DTO.InscripcionEventoDTO;
import com.huahuacuna.app.model.Evento;
import com.huahuacuna.app.model.InscripcionEvento;
import com.huahuacuna.app.repository.EventoRepository;
import com.huahuacuna.app.repository.InscripcionEventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private InscripcionEventoRepository inscripcionRepository;

    // CRUD Eventos
    @Transactional
    public EventoDTO crearEvento(EventoDTO dto) {
        Evento evento = new Evento();
        evento.setTitulo(dto.getTitulo());
        evento.setDescripcion(dto.getDescripcion());
        evento.setHorario(dto.getHorario());
        evento.setLugar(dto.getLugar());
        evento.setImagenUrl(dto.getImagenUrl());
        evento.setDescripcionDetallada(dto.getDescripcionDetallada());
        evento.setFechaEvento(dto.getFechaEvento());
        evento.setActivo(dto.getActivo() != null ? dto.getActivo() : true);

        Evento guardado = eventoRepository.save(evento);
        return convertirADTO(guardado);
    }

    public List<EventoDTO> listarTodos() {
        return eventoRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<EventoDTO> listarActivos() {
        return eventoRepository.findByActivoTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<EventoDTO> listarProximosEventos() {
        return eventoRepository.findByActivoTrueOrderByFechaEventoAsc().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public EventoDTO obtenerPorId(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        return convertirADTO(evento);
    }

    @Transactional
    public EventoDTO actualizar(Long id, EventoDTO dto) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        evento.setTitulo(dto.getTitulo());
        evento.setDescripcion(dto.getDescripcion());
        evento.setHorario(dto.getHorario());
        evento.setLugar(dto.getLugar());
        evento.setImagenUrl(dto.getImagenUrl());
        evento.setDescripcionDetallada(dto.getDescripcionDetallada());
        evento.setFechaEvento(dto.getFechaEvento());
        evento.setActivo(dto.getActivo());

        Evento actualizado = eventoRepository.save(evento);
        return convertirADTO(actualizado);
    }

    @Transactional
    public void eliminar(Long id) {
        eventoRepository.deleteById(id);
    }

    // Inscripciones
    @Transactional
    public InscripcionEvento inscribirse(InscripcionEventoDTO dto) {
        Evento evento = eventoRepository.findById(dto.getEventoId())
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        InscripcionEvento inscripcion = new InscripcionEvento();
        inscripcion.setEvento(evento);
        inscripcion.setNombreCompleto(dto.getNombreCompleto());
        inscripcion.setEmail(dto.getEmail());
        inscripcion.setTelefono(dto.getTelefono());

        return inscripcionRepository.save(inscripcion);
    }

    public List<InscripcionEvento> obtenerInscripciones(Long eventoId) {
        return inscripcionRepository.findByEventoId(eventoId);
    }

    public List<InscripcionEvento> listarTodasInscripciones() {
        return inscripcionRepository.findAll();
    }

    @Transactional
    public InscripcionEvento actualizarEstadoInscripcion(Long inscripcionId, InscripcionEvento.EstadoInscripcion nuevoEstado) {
        InscripcionEvento inscripcion = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
        inscripcion.setEstado(nuevoEstado);
        return inscripcionRepository.save(inscripcion);
    }

    // Convertidor
    private EventoDTO convertirADTO(Evento evento) {
        EventoDTO dto = new EventoDTO();
        dto.setId(evento.getId());
        dto.setTitulo(evento.getTitulo());
        dto.setDescripcion(evento.getDescripcion());
        dto.setHorario(evento.getHorario());
        dto.setLugar(evento.getLugar());
        dto.setImagenUrl(evento.getImagenUrl());
        dto.setDescripcionDetallada(evento.getDescripcionDetallada());
        dto.setFechaEvento(evento.getFechaEvento());
        dto.setActivo(evento.getActivo());
        return dto;
    }
}