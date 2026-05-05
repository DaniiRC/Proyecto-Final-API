package com.agenda.backend_academico.service;

import com.agenda.backend_academico.dto.*;
import com.agenda.backend_academico.model.Grupo;
import com.agenda.backend_academico.model.Usuario;
import com.agenda.backend_academico.repository.GrupoRepository;
import com.agenda.backend_academico.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio central para la gestión de la lógica de negocio de los grupos o clases.
 * Maneja las uniones, salidas y carga de perfiles de grupos.
 */
@Service
public class GrupoService {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Crea un nuevo grupo asociado al profesor que lo solicita.
     *
     * @param dto Datos del grupo (nombre, descripción, profesorId).
     * @return GrupoResponseDTO con el grupo recién guardado en base de datos.
     */
    @Transactional
    public GrupoResponseDTO crearGrupo(GrupoRequestDTO dto) {
        Usuario profesor = usuarioRepository.findById(dto.getProfesorId())
                .orElseThrow(() -> new IllegalArgumentException("Profesor no encontrado"));

        Grupo grupo = new Grupo(dto.getNombre());
        grupo.setProfesor(profesor);
        grupo.setDescripcion(dto.getDescripcion());
        // El color si quisieras añadirlo a la entidad... lo omitimos ya que en tu entidad original no está
        // grupo.setColor(dto.getColor()); // Si se añade en un futuro

        Grupo grupoGuardado = grupoRepository.save(grupo);
        return mapToResponseDTO(grupoGuardado);
    }

    /**
     * Une a un usuario alumno a un grupo utilizando un código de invitación.
     *
     * @param codigo Código alfanumérico generado por el sistema.
     * @param usuarioId ID del alumno.
     * @return GrupoResponseDTO con los datos actualizados del grupo.
     */
    @Transactional
    public GrupoResponseDTO unirseAGrupo(String codigo, Long usuarioId) {
        Grupo grupo = grupoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (grupo.getAlumnos() == null) {
            grupo.setAlumnos(new ArrayList<>());
        }

        if (!grupo.getAlumnos().contains(usuario)) {
            grupo.getAlumnos().add(usuario);
            grupo = grupoRepository.save(grupo);
        }

        return mapToResponseDTO(grupo);
    }

    /**
     * Elimina a un usuario de la lista de participantes de un grupo.
     * Sirve tanto para alumnos que se marchan voluntariamente, como para expulsiones del profesor.
     *
     * @param grupoId ID del grupo académico.
     * @param usuarioId ID del alumno a eliminar.
     */
    @Transactional
    public void salirDeGrupo(Long grupoId, Long usuarioId) {
        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (grupo.getAlumnos() != null && grupo.getAlumnos().contains(usuario)) {
            grupo.getAlumnos().remove(usuario);
            grupoRepository.save(grupo);
        }
    }

    public List<GrupoResponseDTO> obtenerGruposDeUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        if (usuario.getGruposParticipados() == null) {
            return new ArrayList<>();
        }

        return usuario.getGruposParticipados().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<GrupoResponseDTO> obtenerTodos() {
        return grupoRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public GrupoResponseDTO obtenerPorId(Long id) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado"));
        return mapToResponseDTO(grupo);
    }

    public GrupoResponseDTO buscarPorCodigo(String codigo) {
        Grupo grupo = grupoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado"));
        return mapToResponseDTO(grupo);
    }

    /**
     * Sube y persiste la imagen de portada de un grupo en el sistema de archivos local.
     *
     * @param id ID del grupo.
     * @param foto Objeto MultipartFile de la imagen subida.
     * @return GrupoResponseDTO con la nueva URL de la imagen.
     * @throws IOException Si ocurre un error escribiendo el archivo a disco.
     */
    @Transactional
    public GrupoResponseDTO subirFotoGrupo(Long id, MultipartFile foto) throws IOException {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado"));

        Path directorioImagenes = Paths.get("uploads");
        if (!Files.exists(directorioImagenes)) {
            Files.createDirectories(directorioImagenes);
        }

        String nombreArchivo = UUID.randomUUID().toString() + "_" + foto.getOriginalFilename();
        Path rutaCompleta = directorioImagenes.resolve(nombreArchivo);
        Files.copy(foto.getInputStream(), rutaCompleta);

        grupo.setFotoUrl("uploads/" + nombreArchivo);
        Grupo grupoGuardado = grupoRepository.save(grupo);

        return mapToResponseDTO(grupoGuardado);
    }

    private GrupoResponseDTO mapToResponseDTO(Grupo grupo) {
        GrupoResponseDTO dto = new GrupoResponseDTO();
        dto.setId(grupo.getId());
        dto.setNombre(grupo.getNombre());
        dto.setCodigo(grupo.getCodigo());
        dto.setDescripcion(grupo.getDescripcion());
        dto.setFotoUrl(grupo.getFotoUrl());

        if (grupo.getProfesor() != null) {
            Usuario p = grupo.getProfesor();
            dto.setProfesor(new UsuarioResponseDTO(p.getId(), p.getNombre(), p.getEmail(), p.getFotoUrl()));
        }

        if (grupo.getAlumnos() != null) {
            dto.setAlumnos(grupo.getAlumnos().stream()
                    .map(a -> new UsuarioResponseDTO(a.getId(), a.getNombre(), a.getEmail(), a.getFotoUrl()))
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
