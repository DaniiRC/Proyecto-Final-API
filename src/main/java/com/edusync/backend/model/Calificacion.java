package com.edusync.backend.model;

import jakarta.persistence.*;

/**
 * Entidad que representa la calificación individual de un alumno en un evento concreto.
 * Permite al Admin de un grupo asignar una nota a cada participante de forma independiente.
 */
@Entity
@Table(name = "calificacion",
       uniqueConstraints = @UniqueConstraint(columnNames = {"evento_id", "usuario_id"}))
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private Double nota;

    public Calificacion() {}

    public Calificacion(Evento evento, Usuario usuario, Double nota) {
        this.evento = evento;
        this.usuario = usuario;
        this.nota = nota;
    }

    public Long getId() { return id; }
    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Double getNota() { return nota; }
    public void setNota(Double nota) { this.nota = nota; }
}
