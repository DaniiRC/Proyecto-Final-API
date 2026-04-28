package com.agenda.backend_academico.repository;

import com.agenda.backend_academico.model.Asignatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsignaturaRepository extends JpaRepository<Asignatura, Long> {

    // SELECT * FROM asignatura WHERE grupo_id = ?
    List<Asignatura> findByGrupoId(Long grupoId);
    
}