package com.agenda.backend_academico.repository; // Tu paquete real

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agenda.backend_academico.model.Subtarea;

@Repository
public interface SubtareaRepository extends JpaRepository<Subtarea, Long> {
}