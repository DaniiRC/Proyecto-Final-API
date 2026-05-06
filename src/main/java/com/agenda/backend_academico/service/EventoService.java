package com.agenda.backend_academico.service;

import com.agenda.backend_academico.dto.*;
import com.agenda.backend_academico.model.*;
import com.agenda.backend_academico.repository.AsignaturaRepository;
import com.agenda.backend_academico.repository.EventoRepository;
import com.agenda.backend_academico.repository.GrupoRepository;
import com.agenda.backend_academico.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio central para la lógica de negocio de los eventos y tareas.
 * Se encarga de coordinar la creación, actualización y unificación de agendas (personales y grupales).
 */
@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private AsignaturaRepository asignaturaRepository;

    /**
     * Obtiene y unifica todos los eventos a los que tiene acceso un usuario.
     * Incluye eventos creados por él mismo y eventos vinculados a los grupos en los que participa.
     *
     * @param usuarioId ID del usuario.
     * @return Lista de EventoResponseDTO con la información consolidada.
     */
    public List<EventoResponseDTO> obtenerEventosUnificados(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Map<Long, Evento> mapaEventos = new java.util.LinkedHashMap<>();
        
        // 1. Añadimos los eventos donde yo soy el creador (personales y de grupo)
        for (Evento e : eventoRepository.findByCreadorId(usuarioId)) {
            mapaEventos.put(e.getId(), e);
        }

        // 2. Añadimos los eventos de los grupos a los que pertenezco
        if (usuario.getGruposParticipados() != null) {
            for (Grupo grupo : usuario.getGruposParticipados()) {
                List<Evento> eventosDelGrupo = eventoRepository.findByGrupoId(grupo.getId());
                for (Evento e : eventosDelGrupo) {
                    mapaEventos.put(e.getId(), e);
                }
            }
        }

        return mapaEventos.values().stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    public List<EventoResponseDTO> obtenerEventosDeGrupo(Long grupoId) {
        List<Evento> eventosDelGrupo = eventoRepository.findByGrupoId(grupoId);
        return eventosDelGrupo.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    public EventoResponseDTO obtenerEventoPorId(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado"));
        return mapToResponseDTO(evento);
    }

    /**
     * Crea un nuevo evento, aplicando las validaciones de negocio correspondientes,
     * vinculando al creador y, opcionalmente, a un grupo o asignatura.
     *
     * @param dto Datos del evento provenientes de la petición web.
     * @return EventoResponseDTO con el evento recién persistido.
     */
    @Transactional
    public EventoResponseDTO crearEvento(EventoRequestDTO dto) {
        Evento evento = new Evento();
        evento.setTitulo(dto.getTitulo());
        evento.setDescripcion(dto.getDescripcion());
        evento.setFecha(dto.getFecha());
        evento.setHora(dto.getHora());
        evento.setTipo(dto.getTipo());
        evento.setPesoNota(dto.getPesoNota());
        evento.setDificultad(dto.getDificultad());
        evento.setHorasEstimadas(dto.getHorasEstimadas());
        evento.setFocusMode(dto.isFocusMode());
        evento.setNotaObtenida(dto.getNotaObtenida());
        evento.setRecursosUrls(dto.getRecursosUrls());

        Usuario creador = usuarioRepository.findById(dto.getCreadorId())
                .orElseThrow(() -> new IllegalArgumentException("Creador no encontrado"));
        evento.setCreador(creador);

        if (dto.getGrupoId() != null) {
            Grupo grupo = grupoRepository.findById(dto.getGrupoId()).orElse(null);
            evento.setGrupo(grupo);
        }

        if (dto.getAsignaturaId() != null) {
            Asignatura asignatura = asignaturaRepository.findById(dto.getAsignaturaId()).orElse(null);
            evento.setAsignatura(asignatura);
        }

        if (dto.getSubtareas() != null) {
            List<Subtarea> subtareas = dto.getSubtareas().stream().map(subDto -> {
                Subtarea sub = new Subtarea();
                sub.setTitulo(subDto.getTitulo());
                sub.setCompletada(subDto.isCompletada());
                sub.setEvento(evento);
                return sub;
            }).collect(Collectors.toList());
            evento.setSubtareas(subtareas);
        }

        Evento guardado = eventoRepository.save(evento);
        return mapToResponseDTO(guardado);
    }

    @Transactional
    public void eliminarEvento(Long id) {
        if (!eventoRepository.existsById(id)) {
            throw new IllegalArgumentException("Evento no encontrado");
        }
        eventoRepository.deleteById(id);
    }

    @Transactional
    public EventoResponseDTO actualizarEvento(Long id, EventoRequestDTO eventoActualizado) {
        Evento eventoExistente = eventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado"));

        eventoExistente.setTitulo(eventoActualizado.getTitulo());
        eventoExistente.setDescripcion(eventoActualizado.getDescripcion());
        eventoExistente.setHora(eventoActualizado.getHora());
        eventoExistente.setFecha(eventoActualizado.getFecha());
        eventoExistente.setTipo(eventoActualizado.getTipo());
        eventoExistente.setFocusMode(eventoActualizado.isFocusMode());
        eventoExistente.setNotaObtenida(eventoActualizado.getNotaObtenida());

        if (eventoActualizado.getSubtareas() != null) {
            eventoExistente.getSubtareas().clear();
            
            List<Subtarea> nuevasSubtareas = eventoActualizado.getSubtareas().stream().map(subDto -> {
                Subtarea sub = new Subtarea();
                sub.setTitulo(subDto.getTitulo());
                sub.setCompletada(subDto.isCompletada());
                sub.setEvento(eventoExistente);
                return sub;
            }).collect(Collectors.toList());
            
            eventoExistente.getSubtareas().addAll(nuevasSubtareas);
        }

        Evento eventoGuardado = eventoRepository.save(eventoExistente);
        return mapToResponseDTO(eventoGuardado);
    }

    @Transactional
    public void completarEvento(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado"));
        evento.setCompletado(true);
        eventoRepository.save(evento);
    }

    @Transactional
    public void guardarTiempoInvertido(Long id, Long tiempoInvertido) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado"));
        evento.setTiempoInvertidoFocus(tiempoInvertido);
        eventoRepository.save(evento);
    }

    private EventoResponseDTO mapToResponseDTO(Evento evento) {
        EventoResponseDTO dto = new EventoResponseDTO();
        dto.setId(evento.getId());
        dto.setTitulo(evento.getTitulo());
        dto.setDescripcion(evento.getDescripcion());
        dto.setFecha(evento.getFecha());
        dto.setHora(evento.getHora());
        dto.setTipo(evento.getTipo());
        dto.setPesoNota(evento.getPesoNota());
        dto.setDificultad(evento.getDificultad());
        dto.setHorasEstimadas(evento.getHorasEstimadas());
        dto.setFocusMode(evento.isFocusMode());
        dto.setTiempoInvertidoFocus(evento.getTiempoInvertidoFocus());
        dto.setCompletado(evento.isCompletado());
        dto.setNotaObtenida(evento.getNotaObtenida());
        dto.setRecursosUrls(evento.getRecursosUrls());

        if (evento.getAsignatura() != null) {
            dto.setAsignatura(new EventoResponseDTO.IdNombreDTO(evento.getAsignatura().getId(), evento.getAsignatura().getNombre()));
        }

        if (evento.getGrupo() != null) {
            dto.setGrupo(new EventoResponseDTO.IdNombreDTO(evento.getGrupo().getId(), evento.getGrupo().getNombre()));
        }

        if (evento.getCreador() != null) {
            Usuario c = evento.getCreador();
            dto.setCreador(new UsuarioResponseDTO(c.getId(), c.getNombre(), c.getEmail(), c.getFotoUrl()));
        }

        if (evento.getSubtareas() != null) {
            dto.setSubtareas(evento.getSubtareas().stream()
                    .map(s -> new SubtareaResponseDTO(s.getId(), s.getTitulo(), s.isCompletada()))
                    .collect(Collectors.toList()));
        }

        return dto;
    }
    @Transactional
    public void actualizarNota(Long id, Double nota) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado"));
        evento.setNotaObtenida(nota);
        eventoRepository.save(evento);
    }
}

