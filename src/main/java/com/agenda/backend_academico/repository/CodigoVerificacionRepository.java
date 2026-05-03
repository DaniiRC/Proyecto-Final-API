package com.agenda.backend_academico.repository;

import com.agenda.backend_academico.model.CodigoVerificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CodigoVerificacionRepository extends JpaRepository<CodigoVerificacion, Long> {
    
    Optional<CodigoVerificacion> findByEmailAndCodigoAndUsadoFalse(String email, String codigo);
    
    List<CodigoVerificacion> findByEmailAndUsadoFalse(String email);

    void deleteByFechaCreacionBefore(LocalDateTime fecha);
}
