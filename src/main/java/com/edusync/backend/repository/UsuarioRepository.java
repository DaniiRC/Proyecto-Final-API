package com.edusync.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.edusync.backend.model.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmail(String email);
    boolean existsByEmail(String email);
}
