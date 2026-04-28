package com.agenda.backend_academico.controller;

import com.agenda.backend_academico.repository.SubtareaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subtareas")
public class SubtareaController {

    @Autowired
    private SubtareaRepository subtareaRepository;

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestParam boolean completada) {
        return subtareaRepository.findById(id).map(sub -> {
            sub.setCompletada(completada);
            subtareaRepository.save(sub);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}