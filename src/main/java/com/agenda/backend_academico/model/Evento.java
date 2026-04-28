package com.agenda.backend_academico.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "evento") // Le decimos que apunte a la tabla 'evento' de MySQL
public class Evento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_evento")
	private Long id;

	@Column(nullable = false, length = 150)
	private String titulo;

	@Column(columnDefinition = "TEXT")
	private String descripcion;

	@Column(nullable = false)
	private LocalDate fecha;

	private LocalTime hora;

	@Column(length = 50)
	private String tipo; // Ej: 'Examen', 'Tarea', 'Proyecto'

	@Column(name = "peso_nota")
	private Double pesoNota; // Usamos Double para los decimales (ej. 40.5%)

	private Integer dificultad; // Del 1 al 5

	@Column(name = "horas_estimadas")
	private Integer horasEstimadas;

	private boolean focusMode;

	private Long tiempoInvertidoFocus = 0L;

	private boolean completado;

	@ElementCollection
	private List<String> recursosUrls = new ArrayList<>();

	// --- RELACIÓN 1: ASIGNATURA ---
	@ManyToOne
	@JoinColumn(name = "asignatura_id", nullable = true)
	private Asignatura asignatura;

	// --- RELACIÓN 2: CREADOR (Usuario que subió el evento) ---
	@ManyToOne
	@JoinColumn(name = "id_creador", nullable = false)
	private Usuario creador;

	// --- RELACIÓN 3: SUBTAREAS (Lista TO-DO) ---
	@OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Subtarea> subtareas = new ArrayList<>();

	// --- RELACIÓN 4: GRUPO (Clase a la que pertenece) ---
	// Si es NULL, significa que es un evento Personal de un usuario normal.
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "grupo_id", nullable = true)
	private Grupo grupo;

	// Constructor vacío obligatorio para Spring
	public Evento() {
	}

	// --- GETTERS Y SETTERS ---
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

	public LocalTime getHora() {
		return hora;
	}

	public void setHora(LocalTime hora) {
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

	public Asignatura getAsignatura() {
		return asignatura;
	}

	public void setAsignatura(Asignatura asignatura) {
		this.asignatura = asignatura;
	}

	public Usuario getCreador() {
		return creador;
	}

	public void setCreador(Usuario creador) {
		this.creador = creador;
	}

	public Long getTiempoInvertidoFocus() {
		return tiempoInvertidoFocus;
	}

	public void setTiempoInvertidoFocus(Long tiempoInvertidoFocus) {
		this.tiempoInvertidoFocus = tiempoInvertidoFocus;
	}

	public List<Subtarea> getSubtareas() {
		return subtareas;
	}

	public void setSubtareas(List<Subtarea> subtareas) {
		this.subtareas = subtareas;
		if (subtareas != null) {
			for (Subtarea subtarea : subtareas) {
				subtarea.setEvento(this);
			}
		}
	}

	public boolean isFocusMode() {
		return focusMode;
	}

	public void setFocusMode(boolean focusMode) {
		this.focusMode = focusMode;
	}

	public List<String> getRecursosUrls() {
		return recursosUrls;
	}

	public void setRecursosUrls(List<String> recursosUrls) {
		this.recursosUrls = recursosUrls;
	}

	public Grupo getGrupo() {
		return grupo;
	}

	public void setGrupo(Grupo grupo) {
		this.grupo = grupo;
	}

	public boolean isCompletado() {
		return completado;
	}

	public void setCompletado(boolean completado) {
		this.completado = completado;
	}

	// Método auxiliar para añadir una subtarea bidireccionalmente
	public void addSubtarea(Subtarea subtarea) {
		subtareas.add(subtarea);
		subtarea.setEvento(this);
	}

	// Método auxiliar para eliminar una subtarea bidireccionalmente
	public void removeSubtarea(Subtarea subtarea) {
		subtareas.remove(subtarea);
		subtarea.setEvento(null);
	}
}