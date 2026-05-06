package com.agenda.backend_academico.dto;

import java.time.LocalDate;
import java.util.List;

public class EventoResponseDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private LocalDate fecha;
    private String hora;
    private String tipo;
    private Double pesoNota;
    private Integer dificultad;
    private Integer horasEstimadas;
    private boolean focusMode;
    private Long tiempoInvertidoFocus;
    private boolean completado;
    private Double notaObtenida;
    private List<String> recursosUrls;
    private IdNombreDTO asignatura;
    private IdNombreDTO grupo;

    public static class IdNombreDTO {
        private Long id;
        private String nombre;
        public IdNombreDTO(Long id, String nombre) { this.id = id; this.nombre = nombre; }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
    }
    private UsuarioResponseDTO creador;
    private List<SubtareaResponseDTO> subtareas;

    public EventoResponseDTO() {}

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getPesoNota() {
        return pesoNota;
    }

    public void setPesoNota(Double pesoNota) {
        this.pesoNota = pesoNota;
    }

    public Integer getDificultad() {
        return dificultad;
    }

    public void setDificultad(Integer dificultad) {
        this.dificultad = dificultad;
    }

    public Integer getHorasEstimadas() {
        return horasEstimadas;
    }

    public void setHorasEstimadas(Integer horasEstimadas) {
        this.horasEstimadas = horasEstimadas;
    }

    public boolean isFocusMode() {
        return focusMode;
    }

    public void setFocusMode(boolean focusMode) {
        this.focusMode = focusMode;
    }

    public Long getTiempoInvertidoFocus() {
        return tiempoInvertidoFocus;
    }

    public void setTiempoInvertidoFocus(Long tiempoInvertidoFocus) {
        this.tiempoInvertidoFocus = tiempoInvertidoFocus;
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }

    public Double getNotaObtenida() {
        return notaObtenida;
    }

    public void setNotaObtenida(Double notaObtenida) {
        this.notaObtenida = notaObtenida;
    }

    public List<String> getRecursosUrls() {
        return recursosUrls;
    }

    public void setRecursosUrls(List<String> recursosUrls) {
        this.recursosUrls = recursosUrls;
    }

    public IdNombreDTO getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(IdNombreDTO asignatura) {
        this.asignatura = asignatura;
    }

    public IdNombreDTO getGrupo() {
        return grupo;
    }

    public void setGrupo(IdNombreDTO grupo) {
        this.grupo = grupo;
    }

    public UsuarioResponseDTO getCreador() {
        return creador;
    }

    public void setCreador(UsuarioResponseDTO creador) {
        this.creador = creador;
    }

    public List<SubtareaResponseDTO> getSubtareas() {
        return subtareas;
    }

    public void setSubtareas(List<SubtareaResponseDTO> subtareas) {
        this.subtareas = subtareas;
    }
}
