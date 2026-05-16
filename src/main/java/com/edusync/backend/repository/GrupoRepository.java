package com.edusync.backend.repository;

import com.edusync.backend.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    // Método mágico: Spring crea el SQL solo con leer el nombre del método
    Optional<Grupo> findByCodigo(String codigo);
    
    java.util.List<Grupo> findByProfesorId(Long profesorId);
    java.util.List<Grupo> findByAlumnosId(Long alumnoId);
}
