package com.agenda.backend_academico.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agenda.backend_academico.model.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmail(String email);
    boolean existsByEmail(String email);
}