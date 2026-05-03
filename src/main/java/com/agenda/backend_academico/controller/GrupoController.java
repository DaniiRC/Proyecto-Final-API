package com.agenda.backend_academico.controller;

import com.agenda.backend_academico.dto.GrupoRequestDTO;
import com.agenda.backend_academico.dto.GrupoResponseDTO;
import com.agenda.backend_academico.service.GrupoService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/grupos")
@CrossOrigin(origins = "*") // Importante para evitar problemas de CORS con Android/Web
public class GrupoController {

    @Autowired
    private GrupoService grupoService;

    @PostMapping("/crear")
    public ResponseEntity<GrupoResponseDTO> crearGrupo(
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion, 
            @RequestParam(required = false) String color,
            @RequestParam Long profesorId) {
        try {
            // Adaptamos los parámetros a GrupoRequestDTO para mantener el contrato con el Service
            GrupoRequestDTO dto = new GrupoRequestDTO();
            dto.setNombre(nombre);
            dto.setDescripcion(descripcion);
            dto.setColor(color);
            dto.setProfesorId(profesorId);

            return ResponseEntity.ok(grupoService.crearGrupo(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{codigo}/unirse")
    public ResponseEntity<?> unirseAGrupo(@PathVariable String codigo, @RequestParam Long usuarioId) {
        try {
            return ResponseEntity.ok(grupoService.unirseAGrupo(codigo, usuarioId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Grupo o usuario no encontrado");
        }
    }

    @PostMapping("/{grupoId}/salir")
    public ResponseEntity<?> salirDeGrupo(@PathVariable Long grupoId, @RequestParam Long usuarioId) {
        try {
            grupoService.salirDeGrupo(grupoId, usuarioId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<GrupoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(grupoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoResponseDTO> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(grupoService.obtenerPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<GrupoResponseDTO> buscarPorCodigo(@PathVariable String codigo) {
        try {
            return ResponseEntity.ok(grupoService.buscarPorCodigo(codigo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/foto")
    public ResponseEntity<GrupoResponseDTO> subirFoto(@PathVariable Long id, @RequestParam("foto") MultipartFile foto) {
        try {
            return ResponseEntity.ok(grupoService.subirFotoGrupo(id, foto));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<GrupoResponseDTO>> obtenerGruposDeUsuario(@PathVariable Long usuarioId) {
        try {
            return ResponseEntity.ok(grupoService.obtenerGruposDeUsuario(usuarioId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}