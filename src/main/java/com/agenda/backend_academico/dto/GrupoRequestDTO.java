package com.agenda.backend_academico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class GrupoRequestDTO {

    @NotBlank(message = "El nombre del grupo es obligatorio")
    private String nombre;

    private String descripcion;
    private String color;

    @NotNull(message = "El profesorId es obligatorio")
    private Long profesorId;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Long getProfesorId() { return profesorId; }
    public void setProfesorId(Long profesorId) { this.profesorId = profesorId; }
}
