package com.edusync.backend.repository;

import com.edusync.backend.model.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

    List<Calificacion> findByEventoId(Long eventoId);

    List<Calificacion> findByUsuarioId(Long usuarioId);

    Optional<Calificacion> findByEventoIdAndUsuarioId(Long eventoId, Long usuarioId);

    List<Calificacion> findByEventoGrupoIdAndUsuarioId(Long grupoId, Long usuarioId);
}
