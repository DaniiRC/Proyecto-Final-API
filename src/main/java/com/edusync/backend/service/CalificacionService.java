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

@Service
public class CalificacionService {

    @Autowired
    private CalificacionRepository calificacionRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<CalificacionDTO> obtenerCalificacionesPorEvento(Long eventoId) {
        return calificacionRepository.findByEventoId(eventoId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CalificacionDTO obtenerCalificacionDeUsuarioEnEvento(Long eventoId, Long usuarioId) {
        Optional<Calificacion> calOpt = calificacionRepository.findByEventoIdAndUsuarioId(eventoId, usuarioId);
        return calOpt.map(this::mapToDTO).orElse(null);
    }

    public List<CalificacionDTO> obtenerCalificacionesDeUsuarioEnGrupo(Long grupoId, Long usuarioId) {
        return calificacionRepository.findByEventoGrupoIdAndUsuarioId(grupoId, usuarioId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

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
