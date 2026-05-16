package com.edusync.backend.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "grupos")
public class Grupo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombre;

	@Column(unique = true)
	private String codigo;

	private String descripcion;

	private String fotoUrl;

	@OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL)
	private List<Asignatura> asignaturas = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "profesor_id", nullable = false)
	private Usuario profesor;

	@PrePersist
	public void generarCodigo() {
		this.codigo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}

	// --- RELACIÓN CON ALUMNOS (PARTICIPANTES) ---
	// Muchos usuarios pueden estar en muchos grupos
	@ManyToMany
	@JoinTable(name = "grupo_alumnos", joinColumns = @JoinColumn(name = "grupo_id"), inverseJoinColumns = @JoinColumn(name = "usuario_id"))
	private List<Usuario> alumnos = new ArrayList<>();

	// Constructor vacío obligatorio para Spring Boot
	public Grupo() {
	}

	// Constructor al crear grupo (Genera el código automáticamente)
	public Grupo(String nombre) {
		this.nombre = nombre;
		this.codigo = UUID.randomUUID().toString().substring(0, 8);
	}

	// --- Getters y Setters ---
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	// ¡Los getters y setters del profesor que nos faltaban!
	public Usuario getProfesor() {
		return profesor;
	}

	public void setProfesor(Usuario profesor) {
		this.profesor = profesor;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getFotoUrl() {
		return fotoUrl;
	}

	public void setFotoUrl(String fotoUrl) {
		this.fotoUrl = fotoUrl;
	}

	public List<Usuario> getAlumnos() {
		return alumnos;
	}

	public void setAlumnos(List<Usuario> alumnos) {
		this.alumnos = alumnos;
	}
	

	public List<Asignatura> getAsignaturas() {
		return asignaturas;
	}

	public void setAsignaturas(List<Asignatura> asignaturas) {
		this.asignaturas = asignaturas;
	}

}
