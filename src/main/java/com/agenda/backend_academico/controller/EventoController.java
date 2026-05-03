package com.agenda.backend_academico.controller;

import com.agenda.backend_academico.dto.EventoRequestDTO;
import com.agenda.backend_academico.dto.EventoResponseDTO;
import com.agenda.backend_academico.service.EventoService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<EventoResponseDTO>> obtenerEventosUnificados(@PathVariable Long usuarioId) {
        try {
            return ResponseEntity.ok(eventoService.obtenerEventosUnificados(usuarioId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<EventoResponseDTO> crearEvento(@Valid @RequestBody EventoRequestDTO eventoDTO) {
        try {
            return ResponseEntity.ok(eventoService.crearEvento(eventoDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEvento(@PathVariable Long id) {
        try {
            eventoService.eliminarEvento(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> actualizarEvento(@PathVariable Long id, @Valid @RequestBody EventoRequestDTO eventoActualizado) {
        try {
            return ResponseEntity.ok(eventoService.actualizarEvento(id, eventoActualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> obtenerEventoPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(eventoService.obtenerEventoPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/grupo/{grupoId}")
    public ResponseEntity<List<EventoResponseDTO>> obtenerEventosDeGrupo(@PathVariable Long grupoId) {
        return ResponseEntity.ok(eventoService.obtenerEventosDeGrupo(grupoId));
    }

    @PostMapping("/{id}/completar")
    public ResponseEntity<?> completarEvento(@PathVariable Long id, @RequestParam Long usuarioId) {
        try {
            eventoService.completarEvento(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/tiempo-invertido")
    public ResponseEntity<?> guardarTiempoInvertido(@PathVariable Long id, @RequestParam Long tiempoInvertido) {
        try {
            eventoService.guardarTiempoInvertido(id, tiempoInvertido);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}