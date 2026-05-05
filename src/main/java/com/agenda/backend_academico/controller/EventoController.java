package com.agenda.backend_academico.controller;

import com.agenda.backend_academico.dto.EventoRequestDTO;
import com.agenda.backend_academico.dto.EventoResponseDTO;
import com.agenda.backend_academico.service.EventoService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de eventos y tareas.
 * Proporciona endpoints para el CRUD de eventos, sincronización y seguimiento de tiempo (Pomodoro).
 */
@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    /**
     * Obtiene una lista consolidada de eventos que incluye los personales del usuario
     * y los de todos los grupos a los que pertenece.
     *
     * @param usuarioId Identificador del usuario.
     * @return ResponseEntity con la lista de eventos en formato DTO.
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<EventoResponseDTO>> obtenerEventosUnificados(@PathVariable Long usuarioId) {
        try {
            return ResponseEntity.ok(eventoService.obtenerEventosUnificados(usuarioId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Crea un nuevo evento en la base de datos (personal o vinculado a un grupo).
     *
     * @param eventoDTO DTO con los datos del evento a crear.
     * @return ResponseEntity con el evento creado en formato DTO.
     */
    @PostMapping("/crear")
    public ResponseEntity<EventoResponseDTO> crearEvento(@Valid @RequestBody EventoRequestDTO eventoDTO) {
        try {
            return ResponseEntity.ok(eventoService.crearEvento(eventoDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Elimina un evento de la base de datos por su identificador.
     *
     * @param id Identificador del evento a eliminar.
     * @return ResponseEntity 200 OK si es exitoso, 404 si no existe.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEvento(@PathVariable Long id) {
        try {
            eventoService.eliminarEvento(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Actualiza la información de un evento existente.
     *
     * @param id Identificador del evento a modificar.
     * @param eventoActualizado DTO con los nuevos datos del evento.
     * @return ResponseEntity con el evento actualizado en formato DTO.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> actualizarEvento(@PathVariable Long id, @Valid @RequestBody EventoRequestDTO eventoActualizado) {
        try {
            return ResponseEntity.ok(eventoService.actualizarEvento(id, eventoActualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Recupera la información detallada de un evento específico por su ID.
     *
     * @param id Identificador del evento.
     * @return ResponseEntity con los datos del evento en formato DTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> obtenerEventoPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(eventoService.obtenerEventoPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtiene todos los eventos asociados exclusivamente a un grupo en particular.
     *
     * @param grupoId Identificador del grupo académico.
     * @return ResponseEntity con la lista de eventos del grupo.
     */
    @GetMapping("/grupo/{grupoId}")
    public ResponseEntity<List<EventoResponseDTO>> obtenerEventosDeGrupo(@PathVariable Long grupoId) {
        return ResponseEntity.ok(eventoService.obtenerEventosDeGrupo(grupoId));
    }

    /**
     * Marca o desmarca un evento como completado.
     *
     * @param id Identificador del evento.
     * @param usuarioId Identificador del usuario que realiza la acción (para validaciones opcionales).
     * @return ResponseEntity 200 OK si es exitoso.
     */
    @PostMapping("/{id}/completar")
    public ResponseEntity<?> completarEvento(@PathVariable Long id, @RequestParam Long usuarioId) {
        try {
            eventoService.completarEvento(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Acumula el tiempo invertido en un evento, proveniente del temporizador Pomodoro de la app.
     *
     * @param id Identificador del evento.
     * @param tiempoInvertido Tiempo en milisegundos a sumar al acumulado actual.
     * @return ResponseEntity 200 OK si es exitoso.
     */
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