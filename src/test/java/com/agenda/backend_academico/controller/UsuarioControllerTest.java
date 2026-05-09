package com.agenda.backend_academico.controller;

import com.agenda.backend_academico.model.LoginResponseDTO;
import com.agenda.backend_academico.model.Usuario;
import com.agenda.backend_academico.repository.UsuarioRepository;
import com.agenda.backend_academico.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private com.agenda.backend_academico.service.CodigoVerificacionService codigoVerificacionService;

    @Mock
    private com.agenda.backend_academico.service.FileStorageService fileStorageService;

    @Mock
    private org.springframework.mail.javamail.JavaMailSender mailSender;

    @Mock
    private com.agenda.backend_academico.repository.EventoRepository eventoRepository;

    @Mock
    private com.agenda.backend_academico.repository.GrupoRepository grupoRepository;

    @InjectMocks
    private UsuarioController usuarioController;

    private Usuario credencialesCorrectas;
    private Usuario usuarioRegistrado;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
        objectMapper = new ObjectMapper();
        // Arrange
        credencialesCorrectas = new Usuario();
        credencialesCorrectas.setEmail("admin@test.com");
        credencialesCorrectas.setPassword("Password123");

        usuarioRegistrado = new Usuario();
        usuarioRegistrado.setId(1L);
        usuarioRegistrado.setNombre("Test Admin");
        usuarioRegistrado.setEmail("admin@test.com");
        usuarioRegistrado.setPassword("hashedPassword");
        usuarioRegistrado.setRol("ADMIN");
    }

    @Test
    void login_CredencialesCorrectas_DeberiaRetornar200YTokenJwt() throws Exception {
        // Arrange
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(usuarioRegistrado);
        when(passwordEncoder.matches("Password123", "hashedPassword")).thenReturn(true);
        when(jwtUtils.generateToken("admin@test.com")).thenReturn("mock-jwt-token-valido");

        // Act & Assert
        mockMvc.perform(post("/api/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credencialesCorrectas)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token-valido"))
                .andExpect(jsonPath("$.email").value("admin@test.com"))
                .andExpect(jsonPath("$.rol").value("ADMIN"));
    }

    @Test
    void login_ContrasenaIncorrecta_DeberiaRetornar401Unauthorized() throws Exception {
        // Arrange
        Usuario credencialesMalas = new Usuario();
        credencialesMalas.setEmail("admin@test.com");
        credencialesMalas.setPassword("wrongPass");

        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(usuarioRegistrado);
        when(passwordEncoder.matches("wrongPass", "hashedPassword")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credencialesMalas)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registrar_EmailYaExistente_DeberiaRetornar400BadRequest() throws Exception {
        // Arrange
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(usuarioRegistrado);

        // Act & Assert
        mockMvc.perform(post("/api/usuarios/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credencialesCorrectas)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El email ya está en uso"));
    }
}
