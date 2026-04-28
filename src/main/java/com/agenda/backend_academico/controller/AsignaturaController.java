package com.agenda.backend_academico.controller;

import com.agenda.backend_academico.model.Asignatura;
import com.agenda.backend_academico.model.Grupo;
import com.agenda.backend_academico.repository.AsignaturaRepository;
import com.agenda.backend_academico.repository.GrupoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asignaturas")
@CrossOrigin(origins = "*")
public class AsignaturaController {

    @Autowired
    private AsignaturaRepository asignaturaRepository;
    
    @Autowired
    private GrupoRepository grupoRepository;

    // GET: Pedir todas las asignaturas
    @GetMapping
    public List<Asignatura> obtenerTodas() {
        return asignaturaRepository.findAll();
    }

    // [NUEVO] GET: Pedir las asignaturas de un GRUPO específico (Para el filtro de Android)
    @GetMapping("/grupo/{grupoId}")
    public ResponseEntity<List<Asignatura>> obtenerAsignaturasDeGrupo(@PathVariable Long grupoId) {
        // Asegúrate de añadir el método findByGrupoId(Long grupoId) en tu AsignaturaRepository
        return ResponseEntity.ok(asignaturaRepository.findByGrupoId(grupoId)); 
    }

    // [MODIFICADO] POST: Crear asignatura dentro de un grupo con guardado seguro
    @PostMapping("/grupo/{grupoId}")
    public ResponseEntity<Asignatura> crearAsignatura(@PathVariable Long grupoId, @RequestBody Asignatura nuevaAsignatura) {
        return grupoRepository.findById(grupoId).map(grupo -> {
            nuevaAsignatura.setGrupo(grupo);
            Asignatura guardada = asignaturaRepository.save(nuevaAsignatura);
            return ResponseEntity.ok(guardada);
        }).orElse(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAsignatura(@PathVariable Long id) {
        if (asignaturaRepository.existsById(id)) {
            asignaturaRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}