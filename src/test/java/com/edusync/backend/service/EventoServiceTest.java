package com.edusync.backend.service;

import com.edusync.backend.dto.EventoRequestDTO;
import com.edusync.backend.dto.EventoResponseDTO;
import com.edusync.backend.model.Evento;
import com.edusync.backend.model.Usuario;
import com.edusync.backend.repository.EventoRepository;
import com.edusync.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private EventoService eventoService;

    private Usuario usuarioMock;
    private Evento eventoGuardadoMock;
    private EventoRequestDTO eventoRequestDTO;

    @BeforeEach
    void setUp() {
        // Arrange general: Preparamos los datos simulados que usaremos en las pruebas
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setNombre("Dani");

        eventoGuardadoMock = new Evento();
        eventoGuardadoMock.setId(10L);
        eventoGuardadoMock.setTitulo("Entrega TFG");
        eventoGuardadoMock.setDescripcion("Documentación final");
        eventoGuardadoMock.setFecha(LocalDate.of(2026, 6, 15));
        eventoGuardadoMock.setHora("23:59");
        eventoGuardadoMock.setTipo("Proyecto");
        eventoGuardadoMock.setCreador(usuarioMock);

        eventoRequestDTO = new EventoRequestDTO();
        eventoRequestDTO.setTitulo("Entrega TFG");
        eventoRequestDTO.setDescripcion("Documentación final");
        eventoRequestDTO.setFecha(LocalDate.of(2026, 6, 15));
        eventoRequestDTO.setHora("23:59");
        eventoRequestDTO.setTipo("Proyecto");
        eventoRequestDTO.setCreadorId(1L);
    }

    @Test
    void crearEvento_CaminoFeliz_DeberiaCrearYRetornarEvento() {
        // Arrange: Configuramos los Mocks
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(eventoRepository.save(any(Evento.class))).thenReturn(eventoGuardadoMock);

        // Act: Ejecutamos el método real
        EventoResponseDTO resultado = eventoService.crearEvento(eventoRequestDTO);

        // Assert: Verificamos el comportamiento y el resultado
        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals("Entrega TFG", resultado.getTitulo());
        assertEquals("Dani", resultado.getCreador().getNombre());
        
        verify(usuarioRepository, times(1)).findById(1L);
        verify(eventoRepository, times(1)).save(any(Evento.class));
    }

    @Test
    void obtenerEventoPorId_IdInexistente_DeberiaLanzarExcepcion() {
        // Arrange
        Long idInexistente = 999L;
        when(eventoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            eventoService.obtenerEventoPorId(idInexistente);
        });

        assertEquals("Evento no encontrado", excepcion.getMessage());
        verify(eventoRepository, times(1)).findById(idInexistente);
    }
}
