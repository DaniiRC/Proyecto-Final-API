package com.agenda.backend_academico.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Asignatura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String color;
    private String descripcion;
    
    // ELIMINADOS: codigo y numPersonas (Eso pertenece a Grupo)

    // --- RELACIÓN CON EL GRUPO (NUEVO) ---
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "grupo_id", nullable = true) // nullable = true por si quieres asignaturas libres
    private Grupo grupo;

    // --- RELACIÓN CON EVENTOS (NUEVO) ---
    @OneToMany(mappedBy = "asignatura", cascade = CascadeType.ALL)
    @JsonIgnore // No enviamos todos los eventos cada vez que pedimos una asignatura
    private List<Evento> eventos;
    
    // ELIMINADA: La relación directa con Usuario (ahora el usuario pertenece al Grupo)

    public Asignatura() {}

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

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Grupo getGrupo() {
		return grupo;
	}

	public void setGrupo(Grupo grupo) {
		this.grupo = grupo;
	}

	public List<Evento> getEventos() {
		return eventos;
	}

	public void setEventos(List<Evento> eventos) {
		this.eventos = eventos;
	}
    
}