package com.edusync.backend.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombre;

	@Column(unique = true)
	private String email;

	private String password;

	private String fotoUrl;
	
	private String rol = "USER"; // "USER" o "ADMIN"

	// --- RELACIÓN COMO ALUMNO (NUEVO) ---
	// Un alumno puede estar en muchos grupos, y un grupo tiene muchos alumnos.
	@ManyToMany(mappedBy = "alumnos")
	@JsonIgnore // Evita que al cargar el usuario se carguen todos sus grupos y entremos en
				// bucle
	private List<Grupo> gruposParticipados = new ArrayList<>();

	// --- RELACIÓN COMO PROFESOR (NUEVO) ---
	// Un usuario puede ser profesor de muchos grupos.
	@OneToMany(mappedBy = "profesor")
	@JsonIgnore
	private List<Grupo> gruposCreados = new ArrayList<>();

	public Usuario() {
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFotoUrl() {
		return fotoUrl;
	}

	public void setFotoUrl(String fotoUrl) {
		this.fotoUrl = fotoUrl;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	public List<Grupo> getGruposParticipados() {
		return gruposParticipados;
	}

	public void setGruposParticipados(List<Grupo> gruposParticipados) {
		this.gruposParticipados = gruposParticipados;
	}

	public List<Grupo> getGruposCreados() {
		return gruposCreados;
	}

	public void setGruposCreados(List<Grupo> gruposCreados) {
		this.gruposCreados = gruposCreados;
	}
}
