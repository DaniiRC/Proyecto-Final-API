package com.edusync.backend.controller;

import com.edusync.backend.dto.CalificacionDTO;
import com.edusync.backend.service.CalificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calificaciones")
public class CalificacionController {

    @Autowired
    private CalificacionService calificacionService;

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<CalificacionDTO>> obtenerCalificacionesPorEvento(@PathVariable Long eventoId) {
        return ResponseEntity.ok(calificacionService.obtenerCalificacionesPorEvento(eventoId));
    }

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

    @GetMapping("/grupo/{grupoId}/usuario/{usuarioId}")
    public ResponseEntity<List<CalificacionDTO>> obtenerCalificacionesDeUsuarioEnGrupo(
            @PathVariable Long grupoId, @PathVariable Long usuarioId) {
        return ResponseEntity.ok(calificacionService.obtenerCalificacionesDeUsuarioEnGrupo(grupoId, usuarioId));
    }

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
