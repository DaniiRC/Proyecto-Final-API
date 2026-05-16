package com.edusync.backend.repository; // Tu paquete real

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edusync.backend.model.Subtarea;

@Repository
public interface SubtareaRepository extends JpaRepository<Subtarea, Long> {
}
