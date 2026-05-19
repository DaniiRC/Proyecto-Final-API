package com.edusync.backend.controller;

import com.edusync.backend.dto.CalificacionDTO;
import com.edusync.backend.service.CalificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar las calificaciones académicas de los estudiantes.
 * Proporciona endpoints para recuperar notas por eventos, grupos o usuarios, y guardarlas.
 */
@RestController
@RequestMapping("/api/calificaciones")
public class CalificacionController {

    @Autowired
    private CalificacionService calificacionService;

    /**
     * Recupera la lista de calificaciones registradas para un evento concreto.
     *
     * @param eventoId Identificador único del evento evaluable.
     * @return ResponseEntity con la lista de calificaciones DTO asignadas.
     */
    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<CalificacionDTO>> obtenerCalificacionesPorEvento(@PathVariable Long eventoId) {
        return ResponseEntity.ok(calificacionService.obtenerCalificacionesPorEvento(eventoId));
    }

    /**
     * Obtiene la calificación específica obtenida por un usuario/estudiante en un evento.
     *
     * @param eventoId  Identificador único del evento.
     * @param usuarioId Identificador único del usuario alumno.
     * @return ResponseEntity con la calificación correspondiente, o 404 Not Found si no existe.
     */
    @GetMapping("/evento/{eventoId}/usuario/{usuarioId}")
    public ResponseEntity<CalificacionDTO> obtenerCalificacionDeUsuarioEnEvento(
            @PathVariable Long eventoId, @PathVariable Long usuarioId) {
        CalificacionDTO calificacion = calificacionService.obtenerCalificacionDeUsuarioEnEvento(eventoId, usuarioId);
        if (calificacion != null) {
            return ResponseEntity.ok(calificacion);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Recupera un historial de calificaciones correspondientes a un usuario en todas las
     * tareas evaluables pertenecientes a un grupo en particular.
     *
     * @param grupoId   Identificador único del grupo académico.
     * @param usuarioId Identificador único del alumno.
     * @return ResponseEntity con la lista de calificaciones asociadas en ese grupo.
     */
    @GetMapping("/grupo/{grupoId}/usuario/{usuarioId}")
    public ResponseEntity<List<CalificacionDTO>> obtenerCalificacionesDeUsuarioEnGrupo(
            @PathVariable Long grupoId, @PathVariable Long usuarioId) {
        return ResponseEntity.ok(calificacionService.obtenerCalificacionesDeUsuarioEnGrupo(grupoId, usuarioId));
    }

    /**
     * Guarda o actualiza la nota de un alumno para un evento específico.
     *
     * @param eventoId  Identificador único del evento.
     * @param usuarioId Identificador único del alumno calificado.
     * @param nota      Valor de la calificación numérica.
     * @return ResponseEntity con la calificación guardada, o 400 Bad Request si los datos son erróneos.
     */
    @PostMapping("/evento/{eventoId}/usuario/{usuarioId}")
    public ResponseEntity<CalificacionDTO> guardarCalificacion(
            @PathVariable Long eventoId, 
            @PathVariable Long usuarioId, 
            @RequestParam Double nota) {
        try {
            return ResponseEntity.ok(calificacionService.guardarCalificacion(eventoId, usuarioId, nota));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
