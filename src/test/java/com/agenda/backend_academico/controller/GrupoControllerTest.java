package com.agenda.backend_academico.controller;

import com.agenda.backend_academico.dto.GrupoResponseDTO;
import com.agenda.backend_academico.service.GrupoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class GrupoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GrupoService grupoService;

    @InjectMocks
    private GrupoController grupoController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(grupoController).build();
    }

    @Test
    void obtenerPorId_GrupoExistente_DeberiaRetornar200OK() throws Exception {
        // Arrange
        GrupoResponseDTO grupo = new GrupoResponseDTO();
        grupo.setId(10L);
        grupo.setNombre("Programación Java");
        grupo.setCodigo("JAVA2026");

        when(grupoService.obtenerPorId(10L)).thenReturn(grupo);

        // Act & Assert
        mockMvc.perform(get("/api/grupos/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nombre").value("Programación Java"))
                .andExpect(jsonPath("$.codigo").value("JAVA2026"));
    }

    @Test
    void obtenerPorId_GrupoInexistente_DeberiaRetornar404NotFound() throws Exception {
        // Arrange
        when(grupoService.obtenerPorId(99L)).thenThrow(new IllegalArgumentException("Grupo no encontrado"));

        // Act & Assert
        mockMvc.perform(get("/api/grupos/99"))
                .andExpect(status().isNotFound());
    }
}
