package com.edusync.backend.service;

import com.edusync.backend.dto.GrupoResponseDTO;
import com.edusync.backend.model.Grupo;
import com.edusync.backend.model.Usuario;
import com.edusync.backend.repository.GrupoRepository;
import com.edusync.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GrupoServiceTest {

    @Mock
    private GrupoRepository grupoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private GrupoService grupoService;

    private Grupo grupoMock;
    private Usuario alumnoMock;

    @BeforeEach
    void setUp() {
        // Arrange general
        alumnoMock = new Usuario();
        alumnoMock.setId(2L);
        alumnoMock.setNombre("Alumno Test");

        grupoMock = new Grupo("Desarrollo Aplicaciones Multiplataforma");
        grupoMock.setId(100L);
        grupoMock.setCodigo("ABC123XYZ");
        grupoMock.setAlumnos(new ArrayList<>());
    }

    @Test
    void unirseAGrupo_CodigoValidoYUsuarioExistente_DeberiaUnirAlAlumnoConExito() {
        // Arrange
        when(grupoRepository.findByCodigo("ABC123XYZ")).thenReturn(Optional.of(grupoMock));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(alumnoMock));
        when(grupoRepository.save(any(Grupo.class))).thenReturn(grupoMock);

        // Act
        GrupoResponseDTO resultado = grupoService.unirseAGrupo("ABC123XYZ", 2L);

        // Assert
        assertNotNull(resultado);
        assertTrue(grupoMock.getAlumnos().contains(alumnoMock));
        verify(grupoRepository, times(1)).findByCodigo("ABC123XYZ");
        verify(usuarioRepository, times(1)).findById(2L);
        verify(grupoRepository, times(1)).save(grupoMock);
    }

    @Test
    void unirseAGrupo_CodigoInvalidoODesconocido_DeberiaLanzarExcepcion() {
        // Arrange
        String codigoFalso = "CODIGO_FALSO";
        when(grupoRepository.findByCodigo(codigoFalso)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            grupoService.unirseAGrupo(codigoFalso, 2L);
        });

        assertEquals("Grupo no encontrado", excepcion.getMessage());
        verify(grupoRepository, times(1)).findByCodigo(codigoFalso);
        verify(usuarioRepository, never()).findById(anyLong()); // El flujo se corta antes
    }
}
