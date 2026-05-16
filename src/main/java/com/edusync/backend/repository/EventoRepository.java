package com.edusync.backend.repository;

import com.edusync.backend.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    
    // Busca eventos creados por un usuario específico (tareas personales)
    List<Evento> findByCreadorId(Long usuarioId);

    // Busca eventos que pertenecen a una asignatura de un grupo concreto
    // Al llamarse findByAsignatura_Grupo_Id, Spring entiende que debe:
    // 1. Ir a la Asignatura del evento.
    // 2. Ir al Grupo de esa asignatura.
    // 3. Filtrar por ese ID.
    List<Evento> findByAsignaturaGrupoId(Long grupoId);
    List<Evento> findByGrupoId(Long grupoId); 
    
    @Modifying
    @Query("UPDATE Evento e SET e.creador.id = :nuevoId WHERE e.creador.id = :viejoId AND e.grupo.id IS NOT NULL")
    void reasignarEventosDeGrupo(@org.springframework.data.repository.query.Param("viejoId") Long viejoId, @org.springframework.data.repository.query.Param("nuevoId") Long nuevoId);

    @Modifying
    @Query("DELETE FROM Evento e WHERE e.creador.id = :usuarioId AND e.grupo.id IS NULL")
    void borrarEventosPersonales(@org.springframework.data.repository.query.Param("usuarioId") Long usuarioId);
}
