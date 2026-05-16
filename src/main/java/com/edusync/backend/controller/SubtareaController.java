package com.edusync.backend.controller;

import com.edusync.backend.repository.SubtareaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestionar el estado de las subtareas (checklists).
 * Permite marcar los pasos intermedios de un evento como completados o pendientes.
 */
@RestController
@RequestMapping("/api/subtareas")
public class SubtareaController {

    @Autowired
    private SubtareaRepository subtareaRepository;

    /**
     * Actualiza el estado de completitud de una subtarea específica.
     *
     * @param id Identificador de la subtarea a modificar.
     * @param completada Booleano que indica si la subtarea ha sido finalizada o no.
     * @return ResponseEntity 200 OK si se actualiza correctamente, o 404 si la subtarea no existe.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestParam boolean completada) {
        return subtareaRepository.findById(id).map(sub -> {
            sub.setCompletada(completada);
            subtareaRepository.save(sub);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
