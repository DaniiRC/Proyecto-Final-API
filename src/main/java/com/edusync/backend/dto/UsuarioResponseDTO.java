package com.edusync.backend.dto;

public class UsuarioResponseDTO {
    private Long id;
    private String nombre;
    private String email;
    private String fotoUrl;

    public UsuarioResponseDTO() {}

    public UsuarioResponseDTO(Long id, String nombre, String email, String fotoUrl) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.fotoUrl = fotoUrl;
    }

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

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }
}
