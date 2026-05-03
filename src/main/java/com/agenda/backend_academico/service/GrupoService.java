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

@Service
public class GrupoService {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

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
