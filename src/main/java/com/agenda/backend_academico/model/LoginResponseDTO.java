package com.agenda.backend_academico.model;

public class LoginResponseDTO {

    private String token;
    private Long id;
    private String nombre;
    private String email;
    private String fotoUrl;

    public LoginResponseDTO(String token, Long id, String nombre, String email, String fotoUrl) {
        this.token   = token;
        this.id      = id;
        this.nombre  = nombre;
        this.email   = email;
        this.fotoUrl = fotoUrl;
    }

    // Getters
    public String getToken()   { return token;   }
    public Long   getId()      { return id;      }
    public String getNombre()  { return nombre;  }
    public String getEmail()   { return email;   }
    public String getFotoUrl() { return fotoUrl; }
}
