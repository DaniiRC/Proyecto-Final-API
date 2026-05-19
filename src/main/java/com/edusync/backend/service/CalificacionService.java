package com.edusync.backend.service;

import com.edusync.backend.dto.CalificacionDTO;
import com.edusync.backend.model.Calificacion;
import com.edusync.backend.model.Evento;
import com.edusync.backend.model.Usuario;
import com.edusync.backend.repository.CalificacionRepository;
import com.edusync.backend.repository.EventoRepository;
import com.edusync.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio de negocio para gestionar el dominio de calificaciones.
 * Proporciona la lógica para almacenar, recuperar y mapear calificaciones a objetos DTO.
 */
@Service
public class CalificacionService {

    @Autowired
    private CalificacionRepository calificacionRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Recupera todas las calificaciones vinculadas a un evento académico evaluable.
     *
     * @param eventoId Identificador único del evento.
     * @return Lista de calificaciones representadas como objetos CalificacionDTO.
     */
    public List<CalificacionDTO> obtenerCalificacionesPorEvento(Long eventoId) {
        return calificacionRepository.findByEventoId(eventoId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene la calificación de un estudiante específico en un evento determinado.
     *
     * @param eventoId  Identificador único del evento.
     * @param usuarioId Identificador único del alumno.
     * @return CalificacionDTO con los datos de la nota, o nulo si no se encuentra.
     */
    public CalificacionDTO obtenerCalificacionDeUsuarioEnEvento(Long eventoId, Long usuarioId) {
        Optional<Calificacion> calOpt = calificacionRepository.findByEventoIdAndUsuarioId(eventoId, usuarioId);
        return calOpt.map(this::mapToDTO).orElse(null);
    }

    /**
     * Recupera la lista de todas las calificaciones de un estudiante dentro de un grupo académico.
     *
     * @param grupoId   Identificador único del grupo.
     * @param usuarioId Identificador único del estudiante.
     * @return Lista de CalificacionDTO correspondientes a sus tareas y exámenes del grupo.
     */
    public List<CalificacionDTO> obtenerCalificacionesDeUsuarioEnGrupo(Long grupoId, Long usuarioId) {
        return calificacionRepository.findByEventoGrupoIdAndUsuarioId(grupoId, usuarioId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Guarda o actualiza una calificación académica para un alumno y evento concretos.
     * Si la calificación ya existía para esa combinación, se sobrescribe con la nueva nota.
     *
     * @param eventoId  Identificador del evento evaluable.
     * @param usuarioId Identificador del estudiante calificado.
     * @param nota      Valor numérico de la nota (por ejemplo, de 0.0 a 10.0).
     * @return Objeto CalificacionDTO con la calificación persistida.
     * @throws IllegalArgumentException si el evento o el usuario no existen en el sistema.
     */
    @Transactional
    public CalificacionDTO guardarCalificacion(Long eventoId, Long usuarioId, Double nota) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Calificacion calificacion = calificacionRepository.findByEventoIdAndUsuarioId(eventoId, usuarioId)
                .orElse(new Calificacion(evento, usuario, nota));
        
        calificacion.setNota(nota);
        Calificacion guardada = calificacionRepository.save(calificacion);
        return mapToDTO(guardada);
    }

    /**
     * Convierte una entidad de dominio Calificacion a su representación ligera CalificacionDTO.
     *
     * @param cal Entidad de calificación de origen.
     * @return Objeto DTO mapeado.
     */
    private CalificacionDTO mapToDTO(Calificacion cal) {
        return new CalificacionDTO(
                cal.getId(),
                cal.getEvento().getId(),
                cal.getUsuario().getId(),
                cal.getUsuario().getNombre(),
                cal.getNota()
        );
    }
}
