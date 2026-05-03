package com.agenda.backend_academico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EventoRequestDTO {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    private String descripcion;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    private String hora;
    private String tipo;
    private Double pesoNota;
    private Integer dificultad;
    private Integer horasEstimadas;
    private boolean focusMode;
    
    private List<String> recursosUrls;
    
    private Long asignaturaId;
    private Long grupoId;

    @NotNull(message = "El creador es obligatorio")
    private Long creadorId;

    private List<SubtareaRequestDTO> subtareas;

    // Getters y Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Double getPesoNota() { return pesoNota; }
    public void setPesoNota(Double pesoNota) { this.pesoNota = pesoNota; }

    public Integer getDificultad() { return dificultad; }
    public void setDificultad(Integer dificultad) { this.dificultad = dificultad; }

    public Integer getHorasEstimadas() { return horasEstimadas; }
    public void setHorasEstimadas(Integer horasEstimadas) { this.horasEstimadas = horasEstimadas; }

    public boolean isFocusMode() { return focusMode; }
    public void setFocusMode(boolean focusMode) { this.focusMode = focusMode; }

    public List<String> getRecursosUrls() { return recursosUrls; }
    public void setRecursosUrls(List<String> recursosUrls) { this.recursosUrls = recursosUrls; }

    public Long getAsignaturaId() { return asignaturaId; }
    public void setAsignaturaId(Long asignaturaId) { this.asignaturaId = asignaturaId; }

    public Long getGrupoId() { return grupoId; }
    public void setGrupoId(Long grupoId) { this.grupoId = grupoId; }

    public Long getCreadorId() { return creadorId; }
    public void setCreadorId(Long creadorId) { this.creadorId = creadorId; }

    public List<SubtareaRequestDTO> getSubtareas() { return subtareas; }
    public void setSubtareas(List<SubtareaRequestDTO> subtareas) { this.subtareas = subtareas; }

    // --- Adaptadores para el JSON que envía Android (objetos anidados) ---
    
    @JsonProperty("creador")
    public void setCreadorDesdeJson(Map<String, Object> creadorMap) {
        if (creadorMap != null && creadorMap.get("id") != null) {
            this.creadorId = Long.valueOf(creadorMap.get("id").toString());
        }
    }

    @JsonProperty("grupo")
    public void setGrupoDesdeJson(Map<String, Object> grupoMap) {
        if (grupoMap != null && grupoMap.get("id") != null) {
            this.grupoId = Long.valueOf(grupoMap.get("id").toString());
        }
    }

    @JsonProperty("asignatura")
    public void setAsignaturaDesdeJson(Map<String, Object> asignaturaMap) {
        if (asignaturaMap != null && asignaturaMap.get("id") != null) {
            this.asignaturaId = Long.valueOf(asignaturaMap.get("id").toString());
        }
    }
}
