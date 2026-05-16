package com.edusync.backend.model; // ¡Asegúrate de poner tu paquete real aquí!

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*; // Si usas versiones antiguas de Spring Boot, puede ser javax.persistence.*

@Entity
@Table(name = "subtareas")
public class Subtarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    
    // Aquí controlaremos si el CheckBox está marcado o no
    private boolean completada; 

    // Relación: Muchas subtareas pertenecen a un Evento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    @JsonIgnore // ESTO ES VITAL: Evita un bucle infinito al enviar el JSON a Android
    private Evento evento;

    // Constructores
    public Subtarea() {
    }

    public Subtarea(String titulo, boolean completada) {
        this.titulo = titulo;
        this.completada = completada;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }

    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }
}
