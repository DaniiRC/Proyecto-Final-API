package com.agenda.backend_academico.controller;

import com.agenda.backend_academico.model.Asignatura;
import com.agenda.backend_academico.model.Grupo;
import com.agenda.backend_academico.repository.AsignaturaRepository;
import com.agenda.backend_academico.repository.GrupoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de asignaturas.
 * Permite categorizar los eventos y tareas dentro del contexto de un grupo.
 */
@RestController
@RequestMapping("/api/asignaturas")
@CrossOrigin(origins = "*")
public class AsignaturaController {

    @Autowired
    private AsignaturaRepository asignaturaRepository;
    
    @Autowired
    private GrupoRepository grupoRepository;

    /**
     * Recupera una lista global de todas las asignaturas registradas.
     *
     * @return Lista de asignaturas.
     */
    @GetMapping
    public List<Asignatura> obtenerTodas() {
        return asignaturaRepository.findAll();
    }

    /**
     * Obtiene todas las asignaturas asociadas específicamente a un grupo.
     * Utilizado para alimentar filtros en las vistas del cliente (Android).
     *
     * @param grupoId Identificador del grupo académico.
     * @return ResponseEntity con la lista de asignaturas filtradas por grupo.
     */
    @GetMapping("/grupo/{grupoId}")
    public ResponseEntity<List<Asignatura>> obtenerAsignaturasDeGrupo(@PathVariable Long grupoId) {
        return ResponseEntity.ok(asignaturaRepository.findByGrupoId(grupoId)); 
    }

    /**
     * Crea una nueva asignatura y la asocia de forma segura a un grupo existente.
     *
     * @param grupoId Identificador del grupo al que pertenecerá la asignatura.
     * @param nuevaAsignatura Entidad con los datos de la asignatura a crear.
     * @return ResponseEntity con la asignatura guardada o 400 Bad Request si el grupo no existe.
     */
    @PostMapping("/grupo/{grupoId}")
    public ResponseEntity<Asignatura> crearAsignatura(@PathVariable Long grupoId, @RequestBody Asignatura nuevaAsignatura) {
        return grupoRepository.findById(grupoId).map(grupo -> {
            nuevaAsignatura.setGrupo(grupo);
            Asignatura guardada = asignaturaRepository.save(nuevaAsignatura);
            return ResponseEntity.ok(guardada);
        }).orElse(ResponseEntity.badRequest().build());
    }

    /**
     * Elimina una asignatura de la base de datos por su identificador.
     *
     * @param id Identificador de la asignatura a eliminar.
     * @return ResponseEntity 200 OK si es exitoso, o 404 si no se encuentra.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAsignatura(@PathVariable Long id) {
        if (asignaturaRepository.existsById(id)) {
            asignaturaRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}