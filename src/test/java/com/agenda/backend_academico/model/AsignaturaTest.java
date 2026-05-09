package com.agenda.backend_academico.model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AsignaturaTest {

    @Test
    void settersYGetters_DeberianAsignarYRetornarValoresCorrectamente() {
        // Arrange
        Asignatura asignatura = new Asignatura();
        
        // Act
        asignatura.setId(1L);
        asignatura.setNombre("Programación");
        asignatura.setColor("#FFFFFF");
        asignatura.setDescripcion("Clase de Java");
        asignatura.setEventos(new ArrayList<>());
        
        // Assert
        assertEquals(1L, asignatura.getId());
        assertEquals("Programación", asignatura.getNombre());
        assertEquals("#FFFFFF", asignatura.getColor());
        assertEquals("Clase de Java", asignatura.getDescripcion());
        assertNotNull(asignatura.getEventos());
    }
}
