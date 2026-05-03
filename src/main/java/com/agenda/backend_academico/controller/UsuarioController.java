package com.agenda.backend_academico.controller;

import com.agenda.backend_academico.model.LoginResponseDTO;
import com.agenda.backend_academico.model.Usuario;
import com.agenda.backend_academico.model.Evento;
import com.agenda.backend_academico.model.Grupo;
import com.agenda.backend_academico.repository.UsuarioRepository;
import com.agenda.backend_academico.repository.EventoRepository;
import com.agenda.backend_academico.repository.GrupoRepository;
import com.agenda.backend_academico.security.JwtUtils;
import com.agenda.backend_academico.service.CodigoVerificacionService;
import com.agenda.backend_academico.service.FileStorageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CodigoVerificacionService codigoVerificacionService;

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody Usuario credenciales) {
        Usuario usuario = usuarioRepository.findByEmail(credenciales.getEmail());
        if (usuario != null && passwordEncoder.matches(credenciales.getPassword(), usuario.getPassword())) {
            String token = jwtUtils.generateToken(usuario.getEmail());
            LoginResponseDTO dto = new LoginResponseDTO(
                    token,
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getEmail(),
                    usuario.getFotoUrl()
            );
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    // REGISTRO
    @PostMapping("/registro")
    public ResponseEntity<LoginResponseDTO> registrar(@RequestBody Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()) != null) {
            return ResponseEntity.badRequest().build();
        }
        // Hashear la contraseña antes de persistir
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        Usuario nuevoUsuario = usuarioRepository.save(usuario);
        String token = jwtUtils.generateToken(nuevoUsuario.getEmail());
        LoginResponseDTO dto = new LoginResponseDTO(
                token,
                nuevoUsuario.getId(),
                nuevoUsuario.getNombre(),
                nuevoUsuario.getEmail(),
                nuevoUsuario.getFotoUrl()
        );
        return ResponseEntity.ok(dto);
    }
    
    @PostMapping("/{id}/foto")
    public ResponseEntity<Usuario> subirFoto(@PathVariable Long id, @RequestParam("foto") MultipartFile file) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String fileName = fileStorageService.save(file);
        String urlFoto = "/uploads/" + fileName;
        
        usuario.setFotoUrl(urlFoto);
        usuarioRepository.save(usuario);
        
        return ResponseEntity.ok(usuario);
    }

    // LOGIN CON GOOGLE
    @PostMapping("/google-login")
    public ResponseEntity<LoginResponseDTO> googleLogin(@RequestBody Map<String, String> datos) {
        String email = datos.get("email");
        String nombre = datos.get("nombre");
        String fotoUrl = datos.get("fotoUrl");

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setEmail(email);
            usuario.setFotoUrl(fotoUrl);
            // Usuarios de Google no tienen contraseña local; guardamos string vacío
            usuario.setPassword("");
            usuario = usuarioRepository.save(usuario);
        } else if (fotoUrl != null && (usuario.getFotoUrl() == null || usuario.getFotoUrl().startsWith("http"))) {
            usuario.setFotoUrl(fotoUrl);
            usuario = usuarioRepository.save(usuario);
        }

        String token = jwtUtils.generateToken(usuario.getEmail());
        LoginResponseDTO dto = new LoginResponseDTO(
                token,
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getFotoUrl()
        );
        return ResponseEntity.ok(dto);
    }

    // RECUPERAR CONTRASEÑA - PASO 1: ENVIAR CÓDIGO
    @PostMapping("/enviar-codigo")
    public ResponseEntity<Void> enviarCodigo(@RequestParam String email) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        
        if (usuario != null) {
            try {
                String codigo = codigoVerificacionService.guardarCodigo(email);

                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("ruizcocera.daniel@loscerros.org");
                message.setTo(email);
                message.setSubject("!!! ATENCIÓN: CÓDIGO DE SEGURIDAD !!!");
                message.setText("Hola " + usuario.getNombre() + ",\n\n" +
                        "Tu código de verificación para restablecer la contraseña es: " + codigo + "\n\n" +
                        "Introduce este código en la aplicación para continuar.");
                
                mailSender.send(message);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    // RECUPERAR CONTRASEÑA - PASO 2: VERIFICAR CÓDIGO Y CAMBIAR PASS
    @PostMapping("/verificar-y-cambiar")
    public ResponseEntity<Void> verificarYCambiar(
            @RequestParam String email,
            @RequestParam String codigo,
            @RequestParam String nuevaPassword) {

        boolean esValido = codigoVerificacionService.verificarCodigo(email, codigo);

        if (esValido) {
            Usuario usuario = usuarioRepository.findByEmail(email);
            if (usuario != null) {
                // Hashear la nueva contraseña antes de guardarla
                usuario.setPassword(passwordEncoder.encode(nuevaPassword));
                usuarioRepository.save(usuario);
                return ResponseEntity.ok().build();
            }
        }

        return ResponseEntity.status(401).build(); // Código incorrecto o expirado
    }

    // ELIMINAR CUENTA PERMANENTEMENTE
    @PostMapping("/{id}/eliminar")
    @Transactional
    public ResponseEntity<?> eliminarUsuario(@PathVariable("id") Long id) {
        System.out.println("DEBUG: Iniciando eliminación total del usuario ID: " + id);
        try {
            Usuario usuario = usuarioRepository.findById(id).orElse(null);
            if (usuario == null) return ResponseEntity.notFound().build();

            // 1. TRATAMIENTO DE GRUPOS Y SUS EVENTOS
            List<Grupo> gruposProfesor = grupoRepository.findByProfesorId(id);
            List<Grupo> gruposAlumno = grupoRepository.findByAlumnosId(id);
            Set<Grupo> todosGrupos = new HashSet<>();
            todosGrupos.addAll(gruposProfesor);
            todosGrupos.addAll(gruposAlumno);

            for (Grupo grupo : todosGrupos) {
                boolean esProfesor = grupo.getProfesor().getId().equals(id);
                
                // Quitamos al usuario de la lista de alumnos
                if (grupo.getAlumnos() != null) {
                    grupo.getAlumnos().removeIf(a -> a.getId().equals(id));
                }

                if (esProfesor) {
                    if (grupo.getAlumnos() == null || grupo.getAlumnos().isEmpty()) {
                        // PROFESOR SOLO: Borrar primero todos los eventos del grupo
                        // Esto soluciona el error TransientPropertyValueException
                        List<Evento> eventosDelGrupo = eventoRepository.findByGrupoId(grupo.getId());
                        eventoRepository.deleteAll(eventosDelGrupo);
                        
                        // Ahora sí podemos borrar el grupo (borrará asignaturas en cascada)
                        grupoRepository.delete(grupo);
                    } else {
                        // HAY MÁS GENTE: Reasignar profesor y sus eventos
                        Usuario nuevoProfesor = grupo.getAlumnos().get(0);
                        grupo.setProfesor(nuevoProfesor);
                        eventoRepository.reasignarEventosDeGrupo(id, nuevoProfesor.getId());
                        grupoRepository.save(grupo);
                    }
                } else {
                    // ERES ALUMNO: Reasignar tus eventos de grupo al profesor actual
                    eventoRepository.reasignarEventosDeGrupo(id, grupo.getProfesor().getId());
                    grupoRepository.save(grupo);
                }
            }
            eventoRepository.flush();

            // 2. EVENTOS PERSONALES (Los que no tienen grupo)
            List<Evento> eventosRestantes = eventoRepository.findByCreadorId(id);
            for (Evento e : eventosRestantes) {
                if (e.getGrupo() == null) {
                    eventoRepository.delete(e);
                }
            }
            eventoRepository.flush();

            // 3. BORRADO FINAL DEL USUARIO
            usuarioRepository.delete(usuario);
            usuarioRepository.flush();
            
            System.out.println("DEBUG: Usuario " + id + " eliminado con éxito.");
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            System.err.println("ERROR CRÍTICO: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error técnico: " + e.getMessage());
        }
    }
}