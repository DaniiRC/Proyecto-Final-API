package com.edusync.backend.dto;

import java.util.List;

public class GrupoResponseDTO {
    private Long id;
    private String nombre;
    private String codigo;
    private String descripcion;
    private String fotoUrl;
    private UsuarioResponseDTO profesor;
    private List<UsuarioResponseDTO> alumnos;

    public GrupoResponseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    public UsuarioResponseDTO getProfesor() { return profesor; }
    public void setProfesor(UsuarioResponseDTO profesor) { this.profesor = profesor; }

    public List<UsuarioResponseDTO> getAlumnos() { return alumnos; }
    public void setAlumnos(List<UsuarioResponseDTO> alumnos) { this.alumnos = alumnos; }
}
