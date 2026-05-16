package com.edusync.backend.controller;

import com.edusync.backend.dto.EventoRequestDTO;
import com.edusync.backend.dto.EventoResponseDTO;
import com.edusync.backend.service.EventoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EventoControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private EventoService eventoService;

    @InjectMocks
    private EventoController eventoController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventoController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void crearEvento_DatosValidos_DeberiaRetornar200OK() throws Exception {
        // Arrange
        EventoRequestDTO requestDTO = new EventoRequestDTO();
        requestDTO.setTitulo("Estudiar QA");
        requestDTO.setFecha(LocalDate.of(2026, 5, 20));
        requestDTO.setCreadorId(1L);

        EventoResponseDTO responseDTO = new EventoResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setTitulo("Estudiar QA");

        when(eventoService.crearEvento(any(EventoRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/eventos/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Estudiar QA"));
    }

    @Test
    void obtenerEventoPorId_IdInexistente_DeberiaRetornar404NotFound() throws Exception {
        // Arrange
        Long idInexistente = 99L;
        when(eventoService.obtenerEventoPorId(idInexistente))
                .thenThrow(new IllegalArgumentException("Evento no encontrado"));

        // Act & Assert
        mockMvc.perform(get("/api/eventos/{id}", idInexistente))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearEvento_SinCreador_DeberiaRetornar400BadRequest() throws Exception {
        // Arrange
        EventoRequestDTO requestDTO = new EventoRequestDTO();
        requestDTO.setTitulo("Tarea sin dueño");
        
        org.mockito.Mockito.lenient().when(eventoService.crearEvento(any(EventoRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Creador no encontrado"));

        // Act & Assert
        mockMvc.perform(post("/api/eventos/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }
}
