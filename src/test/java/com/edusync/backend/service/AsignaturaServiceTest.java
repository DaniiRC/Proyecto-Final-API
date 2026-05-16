package com.edusync.backend.service;

import com.edusync.backend.model.Asignatura;
import com.edusync.backend.model.Evento;
import com.edusync.backend.repository.AsignaturaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AsignaturaServiceTest {

    @Mock
    private AsignaturaRepository asignaturaRepository;

    @InjectMocks
    private AsignaturaService asignaturaService;

    private Asignatura asignaturaMock;

    @BeforeEach
    void setUp() {
        // Arrange general
        asignaturaMock = new Asignatura();
        asignaturaMock.setId(1L);
        asignaturaMock.setNombre("Programación Avanzada");
        asignaturaMock.setEventos(new ArrayList<>());
    }

    @Test
    void calcularNotaMedia_ConEventosCalificados_DeberiaCalcularMediaPonderada() {
        // Arrange
        Evento parcial = new Evento();
        parcial.setNotaObtenida(8.0);
        parcial.setPesoNota(40.0); // 40%

        Evento proyecto = new Evento();
        proyecto.setNotaObtenida(9.5);
        proyecto.setPesoNota(60.0); // 60%

        List<Evento> eventos = new ArrayList<>();
        eventos.add(parcial);
        eventos.add(proyecto);
        asignaturaMock.setEventos(eventos);

        when(asignaturaRepository.findById(1L)).thenReturn(Optional.of(asignaturaMock));

        // Act
        Double media = asignaturaService.calcularNotaMedia(1L);

        // Assert
        // Cálculo: (8.0 * 0.4) + (9.5 * 0.6) = 3.2 + 5.7 = 8.9
        assertEquals(8.9, media, 0.01);
    }

    @Test
    void calcularNotaMedia_SinEventosCalificados_DeberiaRetornarCero() {
        // Arrange
        Evento tareaSinNota = new Evento();
        tareaSinNota.setPesoNota(20.0); // Aún no tiene nota
        
        List<Evento> eventos = new ArrayList<>();
        eventos.add(tareaSinNota);
        asignaturaMock.setEventos(eventos);

        when(asignaturaRepository.findById(1L)).thenReturn(Optional.of(asignaturaMock));

        // Act
        Double media = asignaturaService.calcularNotaMedia(1L);

        // Assert
        assertEquals(0.0, media);
    }

    @Test
    void calcularNotaMedia_AsignaturaInexistente_DeberiaLanzarExcepcion() {
        // Arrange
        when(asignaturaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            asignaturaService.calcularNotaMedia(99L);
        });

        assertEquals("Asignatura no encontrada", exception.getMessage());
    }
}
