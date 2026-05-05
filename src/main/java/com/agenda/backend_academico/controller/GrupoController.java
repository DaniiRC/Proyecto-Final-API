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

/**
 * Controlador REST para la gestión de grupos académicos (clases).
 * Permite a los profesores crear grupos, y a los alumnos unirse mediante códigos de invitación.
 */
@RestController
@RequestMapping("/api/grupos")
@CrossOrigin(origins = "*")
public class GrupoController {

    @Autowired
    private GrupoService grupoService;

    /**
     * Crea un nuevo grupo académico, generando automáticamente un código de invitación único.
     *
     * @param nombre Nombre descriptivo del grupo.
     * @param descripcion Detalles o descripción opcional del grupo.
     * @param color Color en formato hexadecimal para identificar el grupo.
     * @param profesorId Identificador del usuario creador (profesor/administrador del grupo).
     * @return ResponseEntity con el grupo creado en formato DTO.
     */
    @PostMapping("/crear")
    public ResponseEntity<GrupoResponseDTO> crearGrupo(
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion, 
            @RequestParam(required = false) String color,
            @RequestParam Long profesorId) {
        try {
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

    /**
     * Permite a un alumno unirse a un grupo existente utilizando su código único de invitación.
     *
     * @param codigo Código alfanumérico del grupo.
     * @param usuarioId Identificador del usuario que desea unirse.
     * @return ResponseEntity con los detalles del grupo si es exitoso, o 404 si falla.
     */
    @PostMapping("/{codigo}/unirse")
    public ResponseEntity<?> unirseAGrupo(@PathVariable String codigo, @RequestParam Long usuarioId) {
        try {
            return ResponseEntity.ok(grupoService.unirseAGrupo(codigo, usuarioId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Grupo o usuario no encontrado");
        }
    }

    /**
     * Permite a un alumno salir voluntariamente de un grupo, o a un profesor expulsar a un alumno.
     *
     * @param grupoId Identificador del grupo.
     * @param usuarioId Identificador del usuario que sale o es expulsado.
     * @return ResponseEntity 200 OK si es exitoso.
     */
    @PostMapping("/{grupoId}/salir")
    public ResponseEntity<?> salirDeGrupo(@PathVariable Long grupoId, @RequestParam Long usuarioId) {
        try {
            grupoService.salirDeGrupo(grupoId, usuarioId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Recupera una lista de todos los grupos registrados en el sistema.
     * Utilizado principalmente para tareas administrativas.
     *
     * @return ResponseEntity con la lista de todos los grupos.
     */
    @GetMapping
    public ResponseEntity<List<GrupoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(grupoService.obtenerTodos());
    }

    /**
     * Obtiene la información detallada de un grupo por su ID.
     *
     * @param id Identificador del grupo.
     * @return ResponseEntity con los datos del grupo.
     */
    @GetMapping("/{id}")
    public ResponseEntity<GrupoResponseDTO> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(grupoService.obtenerPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Busca y devuelve la información de un grupo utilizando su código de invitación.
     * Útil para vistas previas antes de confirmar unirse.
     *
     * @param codigo Código de invitación del grupo.
     * @return ResponseEntity con los datos del grupo, o 404 si no se encuentra.
     */
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<GrupoResponseDTO> buscarPorCodigo(@PathVariable String codigo) {
        try {
            return ResponseEntity.ok(grupoService.buscarPorCodigo(codigo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Sube y actualiza la foto de portada de un grupo.
     *
     * @param id Identificador del grupo.
     * @param foto Archivo de imagen multiparte.
     * @return ResponseEntity con los datos actualizados del grupo.
     */
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

    /**
     * Devuelve una lista de todos los grupos a los que pertenece un usuario,
     * ya sea como profesor/creador o como alumno participante.
     *
     * @param usuarioId Identificador del usuario.
     * @return ResponseEntity con la lista de grupos asociados.
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<GrupoResponseDTO>> obtenerGruposDeUsuario(@PathVariable Long usuarioId) {
        try {
            return ResponseEntity.ok(grupoService.obtenerGruposDeUsuario(usuarioId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}