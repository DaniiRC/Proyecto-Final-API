package com.edusync.backend.service;

import com.edusync.backend.model.Asignatura;
import com.edusync.backend.model.Evento;
import com.edusync.backend.repository.AsignaturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AsignaturaService {

    @Autowired
    private AsignaturaRepository asignaturaRepository;

    /**
     * Calcula la media ponderada de una asignatura basándose en los eventos calificados.
     * Solo tiene en cuenta los eventos que tienen notaObtenida y pesoNota definidos.
     * 
     * @param asignaturaId El ID de la asignatura
     * @return La nota media sobre 10, o 0.0 si no hay eventos calificados.
     */
    public Double calcularNotaMedia(Long asignaturaId) {
        Asignatura asignatura = asignaturaRepository.findById(asignaturaId)
                .orElseThrow(() -> new IllegalArgumentException("Asignatura no encontrada"));

        List<Evento> eventos = asignatura.getEventos();
        if (eventos == null || eventos.isEmpty()) {
            return 0.0;
        }

        double notaTotal = 0.0;
        double pesoAcumulado = 0.0;

        for (Evento e : eventos) {
            if (e.getNotaObtenida() != null && e.getPesoNota() != null) {
                // El peso viene en porcentaje (ej: 40 para 40%)
                notaTotal += e.getNotaObtenida() * (e.getPesoNota() / 100.0);
                pesoAcumulado += e.getPesoNota();
            }
        }

        if (pesoAcumulado == 0.0) {
            return 0.0;
        }
        
        return Math.round(notaTotal * 100.0) / 100.0;
    }
}
