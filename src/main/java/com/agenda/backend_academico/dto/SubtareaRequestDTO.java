package com.agenda.backend_academico.dto;

import jakarta.validation.constraints.NotBlank;

public class SubtareaRequestDTO {

    private Long id;

    @NotBlank(message = "El título de la subtarea es obligatorio")
    private String titulo;

    private boolean completada;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }
}
