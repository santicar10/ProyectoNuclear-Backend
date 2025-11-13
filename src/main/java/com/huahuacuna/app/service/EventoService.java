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

/**
 * Servicio que encapsula la lógica de negocio relacionada con eventos e inscripciones.
 *
 * <p>Responsabilidades principales:</p>
 * <ul>
 *   <li>Crear, actualizar, eliminar y recuperar eventos.</li>
 *   <li>Listar eventos (todos, activos, próximos).</li>
 *   <li>Gestionar inscripciones a eventos: crear inscripciones, listar y actualizar su estado.</li>
 *   <li>Convertir entidades {@link Evento} a {@link EventoDTO} para uso en capas superiores (controladores).</li>
 * </ul>
 *
 * <p>Notas importantes:</p>
 * <ul>
 *   <li>Los métodos que modifican datos están anotados con {@code @Transactional} para asegurar
 *       la coherencia durante operaciones de escritura.</li>
 *   <li>Si en {@link EventoDTO#getActivo()} se pasa {@code null} al crear un evento, por defecto
 *       se establece {@code true} (evento activo).</li>
 * </ul>
 *
 * @see EventoRepository
 * @see InscripcionEventoRepository
 * @see EventoDTO
 * @see InscripcionEventoDTO
 * @author Janka033
 * @since 1.0
 */
@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private InscripcionEventoRepository inscripcionRepository;

    // CRUD Eventos

    /**
     * Crea un nuevo evento a partir de la información del DTO y lo persiste.
     *
     * <p>Si el campo {@code activo} en el DTO es {@code null}, se asigna {@code true} por defecto.</p>
     *
     * @param dto DTO con los datos del evento.
     * @return {@link EventoDTO} representando el evento creado.
     */
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

    /**
     * Devuelve todos los eventos existentes sin aplicar filtros.
     *
     * @return Lista de {@link EventoDTO}.
     */
    public List<EventoDTO> listarTodos() {
        return eventoRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Devuelve los eventos marcados como activos.
     *
     * @return Lista de {@link EventoDTO} activos.
     */
    public List<EventoDTO> listarActivos() {
        return eventoRepository.findByActivoTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Devuelve los eventos activos ordenados por fecha de evento ascendente (próximos).
     *
     * @return Lista de {@link EventoDTO} próximos.
     */
    public List<EventoDTO> listarProximosEventos() {
        return eventoRepository.findByActivoTrueOrderByFechaEventoAsc().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un evento por su identificador y lo convierte a DTO.
     *
     * @param id Identificador del evento.
     * @return {@link EventoDTO} correspondiente.
     * @throws RuntimeException si no se encuentra el evento (mensaje: "Evento no encontrado").
     */
    public EventoDTO obtenerPorId(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        return convertirADTO(evento);
    }

    /**
     * Actualiza un evento existente con los datos del DTO.
     *
     * @param id  Identificador del evento a actualizar.
     * @param dto DTO con los nuevos datos.
     * @return {@link EventoDTO} actualizado.
     * @throws RuntimeException si el evento no existe (mensaje: "Evento no encontrado").
     */
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

    /**
     * Elimina un evento por su id.
     *
     * @param id Identificador del evento a eliminar.
     */
    @Transactional
    public void eliminar(Long id) {
        eventoRepository.deleteById(id);
    }

    // Inscripciones

    /**
     * Crea una inscripción para un evento dado por {@code dto.eventoId}.
     *
     * @param dto DTO con los datos de inscripción (eventoId, nombreCompleto, email, telefono).
     * @return {@link InscripcionEvento} persistida.
     * @throws RuntimeException si el evento referenciado no existe (mensaje: "Evento no encontrado").
     */
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

    /**
     * Obtiene las inscripciones asociadas a un evento.
     *
     * @param eventoId Identificador del evento.
     * @return Lista de {@link InscripcionEvento} para el evento.
     */
    public List<InscripcionEvento> obtenerInscripciones(Long eventoId) {
        return inscripcionRepository.findByEventoId(eventoId);
    }

    /**
     * Lista todas las inscripciones de todos los eventos.
     *
     * @return Lista de {@link InscripcionEvento}.
     */
    public List<InscripcionEvento> listarTodasInscripciones() {
        return inscripcionRepository.findAll();
    }

    /**
     * Actualiza el estado de una inscripción y la persiste.
     *
     * @param inscripcionId Identificador de la inscripción a actualizar.
     * @param nuevoEstado   Nuevo estado a asignar ({@link InscripcionEvento.EstadoInscripcion}).
     * @return {@link InscripcionEvento} actualizado.
     * @throws RuntimeException si la inscripción no existe (mensaje: "Inscripción no encontrada").
     */
    @Transactional
    public InscripcionEvento actualizarEstadoInscripcion(Long inscripcionId, InscripcionEvento.EstadoInscripcion nuevoEstado) {
        InscripcionEvento inscripcion = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
        inscripcion.setEstado(nuevoEstado);
        return inscripcionRepository.save(inscripcion);
    }

    // Convertidor

    /**
     * Convierte una entidad {@link Evento} a {@link EventoDTO}.
     *
     * @param evento Entidad de evento.
     * @return DTO con los datos del evento.
     */
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